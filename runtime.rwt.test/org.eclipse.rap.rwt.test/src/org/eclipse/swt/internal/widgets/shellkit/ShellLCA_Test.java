/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.shellkit;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.Message.CreateOperation;
import org.eclipse.rwt.Message.SetOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.*;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCA;
import org.eclipse.swt.widgets.*;


public class ShellLCA_Test extends TestCase {

  private Display display;

  public void testPreserveValues() {
    Shell shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.PUSH );
    Boolean hasListeners;
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( ShellLCA.PROP_SHELL_LISTENER );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( Boolean.FALSE, hasListeners );
    assertEquals( null, adapter.getPreserved( ShellLCA.PROP_ACTIVE_CONTROL ) );
    assertEquals( null, adapter.getPreserved( ShellLCA.PROP_ACTIVE_SHELL ) );
    assertEquals( null, adapter.getPreserved( ShellLCA.PROP_MODE ) );
    assertEquals( new Point( 80, 2 ), adapter.getPreserved( ShellLCA.PROP_MINIMUM_SIZE ) );
    Fixture.clearPreserved();
    shell.setText( "some text" );
    shell.open();
    shell.setActive();
    IShellAdapter shellAdapter
     = ( IShellAdapter )shell.getAdapter( IShellAdapter.class );
    shellAdapter.setActiveControl( button );
    shell.addShellListener( new ShellAdapter() { } );
    shell.setMaximized( true );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setImage( image );
    shell.setMinimumSize( 100, 100 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( ShellLCA.PROP_SHELL_LISTENER );
    assertEquals( "some text", adapter.getPreserved( Props.TEXT ) );
    assertEquals( image, adapter.getPreserved( Props.IMAGE ) );
    assertEquals( Boolean.TRUE, hasListeners );
    assertEquals( button, adapter.getPreserved( ShellLCA.PROP_ACTIVE_CONTROL ) );
    assertEquals( shell, adapter.getPreserved( ShellLCA.PROP_ACTIVE_SHELL ) );
    assertEquals( "maximized", adapter.getPreserved( ShellLCA.PROP_MODE ) );
    assertEquals( new Point( 100, 100 ), adapter.getPreserved( ShellLCA.PROP_MINIMUM_SIZE ) );
    Fixture.clearPreserved();
    //control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    shell.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    shell.setEnabled( true );
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    shell.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( shell );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    shell.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 100, 150 );
    shell.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    shell.addControlListener( new ControlAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    shell.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    shell.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    shell.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( null, shell.getToolTipText() );
    Fixture.clearPreserved();
    shell.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    assertEquals( "some text", shell.getToolTipText() );
    Fixture.clearPreserved();
    //activate_listeners   Focus_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    shell.addFocusListener( new FocusAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( shell, new ActivateAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( shell );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
  }

  public void testReadDataForClosed() {
    final StringBuffer log = new StringBuffer();
    Shell shell = new Shell( display, SWT.NONE );
    shell.open();
    shell.addShellListener( new ShellAdapter() {

      public void shellClosed( final ShellEvent event ) {
        log.append( "closed" );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    Fixture.readDataAndProcessAction( shell );
    assertEquals( "closed", log.toString() );
  }

  public void testReadDataForActiveControl() {
    Shell shell = new Shell( display, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    Label otherLabel = new Label( shell, SWT.NONE );
    String shellId = WidgetUtil.getId( shell );
    String labelId = WidgetUtil.getId( label );
    String otherLabelId = WidgetUtil.getId( otherLabel );
    setActiveControl( shell, otherLabel );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( shellId + ".activeControl", labelId );
    Fixture.readDataAndProcessAction( display );
    assertSame( label, getActiveControl( shell ) );
    // Ensure that if there is both, an avtiveControl parameter and a
    // controlActivated event, the activeControl parameter is ignored
    setActiveControl( shell, otherLabel );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( shellId + ".activeControl", otherLabelId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, labelId );
    Fixture.readDataAndProcessAction( display );
    assertSame( label, getActiveControl( shell ) );
  }

  public void testReadDataForMode() {
    Shell shell = new Shell( display, SWT.NONE );
    shell.open();
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    String shellId = WidgetUtil.getId( shell );
    Fixture.fakeRequestParam( shellId + ".mode", "maximized" );
    Fixture.readDataAndProcessAction( shell );
    assertTrue( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    Fixture.fakeRequestParam( shellId + ".mode", "minimized" );
    Fixture.readDataAndProcessAction( shell );
    assertFalse( shell.getMaximized() );
    assertTrue( shell.getMinimized() );
    Fixture.fakeRequestParam( shellId + ".mode", "null" );
    Fixture.readDataAndProcessAction( shell );
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
  }

  public void testReadModeBoundsOrder() {
    Rectangle displayBounds = new Rectangle( 0, 0, 800, 600 );
    getDisplayAdapter( display ).setBounds( displayBounds );
    Shell shell = new Shell( display );
    Rectangle shellBounds = new Rectangle( 10, 10, 100, 100 );
    shell.setBounds( shellBounds );
    shell.open();
    assertFalse( shell.getMaximized() );
    assertFalse( shell.getMinimized() );
    String shellId = WidgetUtil.getId( shell );
    Fixture.fakeRequestParam( shellId + ".mode", "maximized" );
    Fixture.fakeRequestParam( shellId + ".bounds.width", "800" );
    Fixture.fakeRequestParam( shellId + ".bounds.heigth", "600" );
    Fixture.fakeRequestParam( shellId + ".bounds.x", "0" );
    Fixture.fakeRequestParam( shellId + ".bounds.y", "0" );
    Fixture.readDataAndProcessAction( shell );
    assertEquals( displayBounds, shell.getBounds() );
    Fixture.fakeRequestParam( shellId + ".mode", "null" );
    Fixture.fakeRequestParam( shellId + ".bounds.width", "100" );
    Fixture.fakeRequestParam( shellId + ".bounds.heigth", "100" );
    Fixture.fakeRequestParam( shellId + ".bounds.x", "10" );
    Fixture.fakeRequestParam( shellId + ".bounds.y", "10" );
    Fixture.readDataAndProcessAction( shell );
    assertEquals( shellBounds, shell.getBounds() );
  }

  public void testShellActivate() {
    final StringBuffer activateEventLog = new StringBuffer();
    ActivateListener activateListener = new ActivateListener() {
      public void activated( ActivateEvent event ) {
        Shell shell = ( Shell )event.getSource();
        activateEventLog.append( "activated:" + shell.getData() + "|" );
      }
      public void deactivated( ActivateEvent event ) {
        Shell shell = ( Shell )event.getSource();
        activateEventLog.append( "deactivated:" + shell.getData() + "|" );
      }
    };
    final StringBuffer shellEventLog = new StringBuffer();
    ShellListener shellListener = new ShellAdapter() {
      public void shellActivated( ShellEvent event ) {
        Shell shell = ( Shell )event.getSource();
        shellEventLog.append( "activated:" + shell.getData() + "|" );
      }
      public void shellDeactivated( ShellEvent event ) {
        Shell shell = ( Shell )event.getSource();
        shellEventLog.append( "deactivated:" + shell.getData() + "|" );
      }
    };
    Shell shellToActivate = new Shell( display, SWT.NONE );
    shellToActivate.setData( "shellToActivate" );
    shellToActivate.open();
    Shell activeShell = new Shell( display, SWT.NONE );
    activeShell.setData( "activeShell" );
    activeShell.open();
    String shellToActivateId = WidgetUtil.getId( shellToActivate );
    // Set precondition and assert it
    activeShell.setActive();
    assertSame( activeShell, display.getActiveShell() );
    // Simulate shell activation without event listeners
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_ACTIVATED, shellToActivateId );
    Fixture.executeLifeCycleFromServerThread( );
    assertSame( shellToActivate, display.getActiveShell() );
    // Set precondition and assert it
    Fixture.markInitialized( activeShell );
    Fixture.markInitialized( shellToActivate );
    activeShell.setActive();
    assertSame( activeShell, display.getActiveShell() );

    // Simulate shell activation with event listeners
    ActivateEvent.addListener( shellToActivate, activateListener );
    ActivateEvent.addListener( activeShell, activateListener );
    shellToActivate.addShellListener( shellListener );
    activeShell.addShellListener( shellListener );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_ACTIVATED, shellToActivateId );
    Fixture.executeLifeCycleFromServerThread( );
    assertSame( shellToActivate, display.getActiveShell() );
    String expected = "deactivated:activeShell|activated:shellToActivate|";
    assertEquals( expected, activateEventLog.toString() );
    assertEquals( expected, shellEventLog.toString() );
    // Ensure that no setActive javaScript code is rendered for client-side
    // activated Shell
    assertEquals( -1, Fixture.getAllMarkup().indexOf( "setActive" ) );
  }

  public void testNoDeactivateNullActiveShell() {
    Shell shell1 = new Shell( display );
    shell1.setVisible( true );
    Shell shell2 = new Shell( display );
    shell2.setVisible( true );
    assertNull( display.getActiveShell() );
    // creating an event with null source throws exception
    try {
      new ActivateEvent( null, ActivateEvent.DEACTIVATED );
      fail();
    } catch( IllegalArgumentException e ) {
      // expected
    }
    // no deactivation event must be created for a null active shell
    Fixture.fakeNewRequest( display );
    String shell1Id = WidgetUtil.getId( shell1 );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_ACTIVATED, shell1Id );
    Fixture.readDataAndProcessAction( display );
    assertSame( shell1, display.getActiveShell() );
  }

  public void testDisposeSingleShell() {
    Shell shell = new Shell( display );
    shell.open();
    String shellId = WidgetUtil.getId( shell );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_SHELL_CLOSED, shellId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 0, display.getShells().length );
    assertEquals( null, display.getActiveShell() );
    assertEquals( true, shell.isDisposed() );
  }

  public void testAlpha() throws Exception {
    Shell shell = new Shell( display );
    shell.setAlpha( 23 );
    shell.open();
    AbstractWidgetLCA lca = WidgetUtil.getLCA( shell );
    Fixture.fakeResponseWriter();
    lca.renderInitialization( shell );
    lca.renderChanges( shell );
    String expected = "w.setOpacity( 0.09 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    //
    Fixture.fakeResponseWriter();
    shell.setAlpha( 250 );
    lca.renderChanges( shell );
    expected = "w.setOpacity( 0.98 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testWriteInitialBounds() throws Exception {
    Shell shell = new Shell( display , SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeBounds( shell );
    String notExpected = "w.setSpace( 0, 0, 0, 0 );";
    assertTrue( Fixture.getAllMarkup().indexOf( notExpected ) == -1 );
  }

  public void testWriteMinimumSize() throws Exception {
    Shell shell = new Shell( display , SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    shell.setMinimumSize( 100, 100 );
    Fixture.fakeResponseWriter();
    ShellLCA lca = new ShellLCA();
    lca.renderChanges( shell );
    String expected = "w.setMinWidth( 100 );w.setMinHeight( 100 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
  }

  public void testWriteStyleFlags() throws Exception {
    Shell shell = new Shell( display , SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ShellLCA lca = new ShellLCA();

    lca.renderInitialization( shell );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = ( CreateOperation )message.getOperation( 0 );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "NO_TRIM" ) );
  }

  public void testWriteText() throws Exception {
    Shell shell = new Shell( display , SWT.SHELL_TRIM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ShellLCA lca = new ShellLCA();

    shell.setText( "foo" );
    lca.renderChanges( shell );
    Message message = Fixture.getProtocolMessage();
    SetOperation operation = ( SetOperation )message.getOperation( 0 );
    assertEquals( "foo", operation.getProperty( "text" ) );
  }

  public void testWriteParentShellForDialogShell() throws Exception {
    Shell parentShell = new Shell( display );
    Shell dialogShell = new Shell( parentShell );
    Fixture.markInitialized( display );
    Fixture.markInitialized( parentShell );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ShellLCA lca = new ShellLCA();

    lca.renderInitialization( dialogShell );
    
    String parentRef = "wm.findWidgetById( \"" + WidgetUtil.getId( parentShell ) + "\" )";
    String expected = "w.setParentShell( " + parentRef + " );";
    assertTrue( Fixture.getAllMarkup().contains( expected ) );
  }

  public void testTitleImageWithCaptionBar() throws Exception {
    Shell shell = new Shell( display , SWT.SHELL_TRIM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ShellLCA lca = new ShellLCA();
    
    shell.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    lca.renderChanges( shell );
    
    Message message = Fixture.getProtocolMessage();
    SetOperation operation = ( SetOperation )message.getOperation( 0 );
    String expected = ImageFactory.getImagePath( shell.getImage() );
    assertEquals( expected, operation.getProperty( "image" ) );
  }

  public void testTitleImageWithoutCaptionBar() throws Exception {
    Shell shell = new Shell( display, SWT.NO_TRIM );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ShellLCA lca = new ShellLCA();

    shell.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    lca.renderChanges( shell );
    
    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  public void testTitleImageWithCaptionBarWihoutMinMaxClose() throws Exception {
    Shell shell = new Shell( display, SWT.TITLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ShellLCA lca = new ShellLCA();

    shell.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    lca.renderChanges( shell );
    
    Message message = Fixture.getProtocolMessage();
    SetOperation operation = ( SetOperation )message.getOperation( 0 );
    String expected = ImageFactory.getImagePath( shell.getImage() );
    assertEquals( expected, operation.getProperty( "image" ) );
  }

  public void testTitleImageWithMultipleImages() throws Exception {
    Shell shell = new Shell( display, SWT.TITLE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ShellLCA lca = new ShellLCA();

    shell.setImages( new Image[] { Graphics.getImage( Fixture.IMAGE1 ) } );
    lca.renderChanges( shell );
    
    Message message = Fixture.getProtocolMessage();
    SetOperation operation = ( SetOperation )message.getOperation( 0 );
    String expected = ImageFactory.getImagePath( shell.getImages()[0] );
    assertEquals( expected, operation.getProperty( "image" ) );
  }

  // see bug 223879
  public void testRenderPopupMenu() throws IOException {
    Fixture.markInitialized( display );
    Shell shell = new Shell( display , SWT.NONE );
    shell.setMinimumSize( 100, 100 );
    Menu menu = new Menu( shell, SWT.POP_UP );
    shell.setMenu( menu );
    Fixture.fakeResponseWriter();
    Fixture.fakeNewRequest( display );
    new DisplayLCA().render( display );
    String markup = Fixture.getAllMarkup();
    String createMenuScript
      = "var w = new org.eclipse.rwt.widgets.Menu();"
      + "wm.add( w, \"w3\", false );";
    int createMenuScriptIndex = markup.indexOf( createMenuScript );
    String setShellMenuScript = "wm.setContextMenu( wm.findWidgetById( \"w2\" ), w );";
    int setShellMenuScriptIndex = markup.indexOf( setShellMenuScript );
    assertTrue( createMenuScriptIndex != -1 );
    assertTrue( setShellMenuScriptIndex != -1 );
    assertTrue( createMenuScriptIndex < setShellMenuScriptIndex );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static Control getActiveControl( Shell shell ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    return shellAdapter.getActiveControl();
  }

  private static void setActiveControl( Shell shell, Control control ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( control );
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    return ( IDisplayAdapter )adapter;
  }
}
