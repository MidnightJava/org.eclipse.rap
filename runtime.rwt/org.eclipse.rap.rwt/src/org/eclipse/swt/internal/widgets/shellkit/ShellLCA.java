/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.shellkit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;


public final class ShellLCA extends AbstractWidgetLCA {

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_ALPHA = "alpha";
  static final String PROP_ACTIVE_CONTROL = "activeControl";
  static final String PROP_ACTIVE_SHELL = "activeShell";
  static final String PROP_MODE = "mode";
  static final String PROP_FULLSCREEN = "fullScreen";
  static final String PROP_MINIMUM_SIZE = "minimumSize";
  static final String PROP_SHELL_LISTENER = "shellListener";
  private static final String PROP_SHELL_MENU  = "menuBar";
  private static final String PROP_SHELL_MENU_BOUNDS = "menuBarShellClientArea";

  private final static Object[] NULL_PARAMETER = new Object[] { null };

  @Override
  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    Shell shell = ( Shell )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    adapter.preserve( PROP_ACTIVE_CONTROL, getActiveControl( shell ) );
    adapter.preserve( PROP_ACTIVE_SHELL, shell.getDisplay().getActiveShell() );
    adapter.preserve( PROP_TEXT, shell.getText() );
    adapter.preserve( PROP_IMAGE, shell.getImage() );
    adapter.preserve( PROP_ALPHA, new Integer( shell.getAlpha() ) );
    adapter.preserve( PROP_MODE, getMode( shell ) );
    adapter.preserve( PROP_FULLSCREEN, Boolean.valueOf( shell.getFullScreen() ) );
    adapter.preserve( PROP_SHELL_LISTENER, Boolean.valueOf( ShellEvent.hasListener( shell ) ) );
    adapter.preserve( PROP_SHELL_MENU, shell.getMenuBar() );
    adapter.preserve( PROP_MINIMUM_SIZE, shell.getMinimumSize() );
    WidgetLCAUtil.preserveCustomVariant( shell );
  }

  public void readData( Widget widget ) {
    Shell shell = ( Shell )widget;
    // [if] Preserve the menu bounds before setting the new shell bounds.
    preserveMenuBounds( shell );
    // Important: Order matters, readMode() before readBounds()
    readMode( shell );
    readBounds( shell );
    if( WidgetLCAUtil.wasEventSent( shell, JSConst.EVENT_SHELL_CLOSED ) ) {
      shell.close();
    }
    processActiveShell( shell );
    processActivate( shell );
    ControlLCAUtil.processMouseEvents( shell );
    ControlLCAUtil.processKeyEvents( shell );
    ControlLCAUtil.processMenuDetect( shell );
    WidgetLCAUtil.processHelp( shell );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Shell shell = ( Shell )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( shell );
    String[] allowedStyles = RWTStylesUtil.getAllowedStylesForWidget( shell );
    String[] styles = StylesUtil.filterStyles( shell, allowedStyles );
    Map<String, Object> args = new HashMap<String, Object>();
    args.put( "style", styles );
    clientObject.create( args );
    Composite parent = shell.getParent();
    if( parent instanceof Shell ) {
      JSWriter.getWriterFor( widget ).set( "parentShell", parent );
    }
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Shell shell = ( Shell )widget;
    WidgetLCAUtil.writeCustomVariant( shell ); // Order matters for animation
    writeImage( shell );
    writeText( shell );
    writeAlpha( shell );
    // Important: Order matters, writing setActive() before open() leads to
    //            strange behavior!
    writeOpen( shell );
    writeActiveShell( shell );
    // Important: Order matters, write setMode() after open() and before
    // setBounds() - see bug 302224
    writeMode( shell );
    writeFullScreen( shell );
    writeCloseListener( shell );
    writeMinimumSize( shell );
    writeDefaultButton( shell );
    writePopupMenu( shell );
    ControlLCAUtil.writeChanges( shell );
  }

  @Override
  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.call( "doClose", null );
    writer.dispose();
  }

  //////////////////
  // Helping methods

  private static void writeText( Shell shell ) {
    String text = shell.getText();
    if( WidgetLCAUtil.hasChanged( shell, PROP_TEXT, text, "" ) ) {
      text = WidgetLCAUtil.escapeText( text, false );
      IClientObject clientObject = ClientObjectFactory.getForWidget( shell );
      clientObject.setProperty( "text", text );
    }
  }

  private void writeAlpha( Shell shell ) throws IOException {
    int alpha = shell.getAlpha();
    if( WidgetLCAUtil.hasChanged( shell, PROP_ALPHA, new Integer( alpha ), new Integer( 0xFF ) ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      float opacity = ( alpha & 0xFF ) * 1000 / 0xFF / 1000.0f;
      writer.set( "opacity", opacity );
    }
  }

  private static void writeOpen( Shell shell ) throws IOException {
    // TODO [rst] workaround: qx window should be opened only once.
    Boolean defValue = Boolean.FALSE;
    Boolean actValue = Boolean.valueOf( shell.getVisible() );
    if(    WidgetLCAUtil.hasChanged( shell, Props.VISIBLE, actValue, defValue )
        && shell.getVisible() )
    {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.call( "open", null );
    }
  }

  private static void writeMinimumSize( Shell shell ) throws IOException {
    Point newValue = shell.getMinimumSize();
    if( WidgetLCAUtil.hasChanged( shell, PROP_MINIMUM_SIZE, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "minWidth", new Integer( newValue.x ) );
      writer.set( "minHeight", new Integer( newValue.y ) );
    }
  }

  private static void writeDefaultButton( Shell shell ) throws IOException {
    Button defaultButton = shell.getDefaultButton();
    // NOTE [tb] : set happens in PushButtonLCA - can all be handled here?
    if( defaultButton != null && defaultButton.isDisposed() ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.call( "setDefaultButton", NULL_PARAMETER );
    }
  }

  private static void writePopupMenu( final Shell shell ) throws IOException {
    final Menu menu = shell.getMenu();
    if( WidgetLCAUtil.hasChanged( shell, Props.MENU, menu, null ) ) {
      if( menu != null ) {
        WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( menu );
        adapter.setRenderRunnable( new IRenderRunnable() {
          public void afterRender() throws IOException {
            WidgetLCAUtil.writeMenu( shell, menu );
          }
        } );
      } else {
        WidgetLCAUtil.writeMenu( shell, menu );
      }
    }
  }

  /////////////////////////////////////////////
  // Methods to read and write the active shell

  private static void writeActiveShell( Shell shell ) throws IOException {
    Shell activeShell = shell.getDisplay().getActiveShell();
    boolean hasChanged = WidgetLCAUtil.hasChanged( shell, PROP_ACTIVE_SHELL, activeShell, null );
    if( shell == activeShell && hasChanged ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "active", true );
    }
  }

  private static void processActiveShell( Shell shell ) {
    if( WidgetLCAUtil.wasEventSent( shell, JSConst.EVENT_SHELL_ACTIVATED ) ) {
      Shell lastActiveShell = shell.getDisplay().getActiveShell();
      setActiveShell( shell );
      ActivateEvent event;
      if( lastActiveShell != null ) {
        event = new ActivateEvent( lastActiveShell, ActivateEvent.DEACTIVATED );
        event.processEvent();
      }
      event = new ActivateEvent( shell, ActivateEvent.ACTIVATED );
      event.processEvent();
    }
  }

  private static void setActiveShell( Shell shell ) {
    Object adapter = shell.getDisplay().getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setActiveShell( shell );
  }

  /////////////////////////////////////////////////////
  // Methods to handle activeControl and ActivateEvents

  /* (intentionally non-JavaDoc'ed)
   * This method is declared public only to be accessible from DisplayLCA
   */
  public static void writeActiveControl( Shell shell ) throws IOException {
    final Control activeControl = getActiveControl( shell );
    String prop = PROP_ACTIVE_CONTROL;
    if( WidgetLCAUtil.hasChanged( shell, prop, activeControl, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "activeControl", new Object[] { activeControl } );
    }
  }

  // TODO [rh] is this safe for multiple shells?
  private static void processActivate( Shell shell ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String widgetId = request.getParameter( JSConst.EVENT_WIDGET_ACTIVATED );
    if( widgetId != null ) {
      Widget widget = WidgetUtil.find( shell, widgetId );
      if( widget != null ) {
        setActiveControl( shell, widget );
      }
    } else {
      String activeControlId = WidgetLCAUtil.readPropertyValue( shell, "activeControl" );
      Widget widget = WidgetUtil.find( shell, activeControlId );
      if( widget != null ) {
        setActiveControl( shell, widget );
      }
    }
  }

  private static Control getActiveControl( Shell shell ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    Control activeControl = shellAdapter.getActiveControl();
    return activeControl;
  }

  private static void setActiveControl( Shell shell, final Widget widget ) {
    if( EventUtil.isAccessible( widget ) ) {
      Object adapter = shell.getAdapter( IShellAdapter.class );
      IShellAdapter shellAdapter = ( IShellAdapter )adapter;
      shellAdapter.setActiveControl( ( Control )widget );
    }
  }

  private static void writeImage( Shell shell ) {
    if( ( shell.getStyle() & SWT.TITLE ) != 0 ) {
      Image image = shell.getImage();
      if( image == null ) {
        Image[] defaultImages = shell.getImages();
        if( defaultImages.length > 0 ) {
          image = defaultImages[0];
        }
      }
      if( WidgetLCAUtil.hasChanged( shell, PROP_IMAGE, image, null ) ) {
        IClientObject clientObject = ClientObjectFactory.getForWidget( shell );
        clientObject.setProperty( "image", ImageFactory.getImagePath( image ) );
      }
    }
  }

  private static void readBounds( Shell shell ) {
    Rectangle bounds = WidgetLCAUtil.readBounds( shell, shell.getBounds() );
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setBounds( bounds );
  }

  private static void readMode( Shell shell ) {
    final String value = WidgetLCAUtil.readPropertyValue( shell, "mode" );
    if( value != null ) {
      if( "maximized".equals( value ) ) {
        shell.setMaximized( true );
      } else if( "minimized".equals( value ) ) {
        shell.setMinimized( true );
      } else {
        shell.setMinimized( false );
        shell.setMaximized( false );
      }
    }
  }

  private static void writeMode( Shell shell ) throws IOException {
    Object defValue = null;
    Object newValue = getMode( shell );
    if( WidgetLCAUtil.hasChanged( shell, PROP_MODE, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "mode", newValue );
    }
  }

  private static void writeCloseListener( Shell shell ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell );
    Boolean newValue = Boolean.valueOf( ShellEvent.hasListener( shell ) );
    Boolean defValue = Boolean.FALSE;
    writer.set( PROP_SHELL_LISTENER, "hasShellListener", newValue, defValue );
  }

  private static void writeFullScreen( Shell shell ) throws IOException {
    Object defValue = Boolean.FALSE;
    Boolean newValue = Boolean.valueOf( shell.getFullScreen() );
    if( WidgetLCAUtil.hasChanged( shell, PROP_FULLSCREEN, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "fullScreen", newValue );
    }
  }

  private static String getMode( Shell shell ) {
    String result = null;
    if( shell.getMinimized() ) {
      result = "minimized";
    } else if( shell.getMaximized() || shell.getFullScreen() ) {
      result = "maximized";
    }
    return result;
  }

  private static void preserveMenuBounds( Shell shell ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    Rectangle menuBounds = shellAdapter.getMenuBounds();
    IWidgetAdapter widgetAdapter = WidgetUtil.getAdapter( shell );
    widgetAdapter.preserve( PROP_SHELL_MENU_BOUNDS, menuBounds );
  }
}
