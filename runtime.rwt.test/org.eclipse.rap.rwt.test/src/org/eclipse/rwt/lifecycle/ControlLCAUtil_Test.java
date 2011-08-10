/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.protocol.Message;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.buttonkit.ButtonLCA;
import org.eclipse.swt.internal.widgets.compositekit.CompositeLCA;
import org.eclipse.swt.internal.widgets.labelkit.LabelLCA;
import org.eclipse.swt.internal.widgets.shellkit.ShellLCA;
import org.eclipse.swt.widgets.*;
import org.json.*;


public class ControlLCAUtil_Test extends TestCase {

  private static final String WIDGET_DEFAULT_SELECTED = "widgetDefaultSelected";
  private static final String WIDGET_SELECTED = "widgetSelected";

  private Display display;
  private Shell shell;
  private Control control;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    display = new Display();
    shell = new Shell( display );
    control = new Button( shell, SWT.PUSH );
    control.setSize( 10, 10 ); // Would be rendered as invisible otherwise
  }

  @Override
  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testWriteBounds() throws Exception {
    // Ensure that bounds for an uninitialized widget are rendered
    Composite composite = new Composite( shell , SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeBounds( composite );
    // TODO [fappel]: check whether minWidth and minHeight is still needed -
    //                causes problems on FF with caching
    //String expected
    //  = w.setSpace( 0, 0, 0, 0 );w.setMinWidth( 0 );w.setMinHeight( 0 );";
    String expected = "w.setSpace( 0, 0, 0, 0 );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );
    // Ensure that unchanged bound do not lead to markup
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( composite );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    ControlLCAUtil.writeBounds( composite );
    assertEquals( "", Fixture.getAllMarkup() );
    // Ensure that bounds-changes on an already initialized widgets are rendered
    Fixture.fakeResponseWriter();
    composite.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    ControlLCAUtil.writeBounds( composite );
    expected = "w.setSpace( 1, 3, 2, 4 );";
    assertTrue( Fixture.getAllMarkup().endsWith( expected ) );
  }

  public void testWriteToolTip() throws IOException {
    // on a not yet initialized control: no tool tip -> no markup
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    shell.setToolTipText( "" );
    ControlLCAUtil.writeToolTip( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    // on a not yet initialized control: non-empty tool tip must be rendered
    Fixture.fakeResponseWriter();
    shell.setToolTipText( "abc" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "abc" ) != -1 );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( shell );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.setToolTipText( null );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    // on an initialized control: change tooltip from non-empty to empty
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( shell );
    shell.setToolTipText( "abc" );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.setToolTipText( "newTooltip" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    assertTrue( Fixture.getAllMarkup().indexOf( "newTooltip" ) != -1 );
    // on an initialized control: change non-empty tooltip text
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( shell );
    shell.setToolTipText( "newToolTip" );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    shell.setToolTipText( "anotherTooltip" );
    ControlLCAUtil.writeToolTip( shell );
    assertTrue( Fixture.getAllMarkup().indexOf( "setToolTip" ) != -1 );
    assertTrue( Fixture.getAllMarkup().indexOf( "anotherTooltip" ) != -1 );
    // test actual markup - the next two lines fake situation that there is
    // already a widget reference (w)
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.newWidget( "Window" );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeToolTip( shell );
    String expected = "wm.setToolTip( w, \"anotherTooltip\" );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteActivateListener() throws IOException {
    ActivateAdapter listener = new ActivateAdapter() {
    };
    Composite composite = new Composite( shell, SWT.NONE );
    Label label = new Label( composite, SWT.NONE );

    // A non-initialized widget with no listener attached must not render
    // JavaScript code for adding activateListeners
    AbstractWidgetLCA labelLCA = WidgetUtil.getLCA( label );
    Fixture.fakeResponseWriter();
    labelLCA.renderChanges( label );
    String markup = Fixture.getAllMarkup();
    assertEquals( false, WidgetUtil.getAdapter( label ).isInitialized() );
    assertTrue( markup.length() > 0 );
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) == -1 );

    // A non-initialized widget with a listener attached must render JavaScript
    // code for adding activateListeners
    ActivateEvent.addListener( label, listener );
    Fixture.fakeResponseWriter();
    labelLCA.renderChanges( label );
    markup = Fixture.getAllMarkup();
    assertEquals( false, WidgetUtil.getAdapter( label ).isInitialized() );
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) != -1 );

    // An initialized widget with unchanged activateListeners must not render
    // JavaScript code for adding activateListeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( label );
    labelLCA.preserveValues( label );
    labelLCA.renderChanges( label );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) == -1 );

    // Removing an ActivateListener from an initialized widget must render
    // JavaScript code for removing activateListeners
    Fixture.fakeResponseWriter();
    Fixture.markInitialized( label );
    labelLCA.preserveValues( label );
    ActivateEvent.removeListener( label, listener );
    labelLCA.renderChanges( label );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "addActivateListenerWidget" ) == -1 );
    assertTrue( markup.indexOf( "removeActivateListenerWidget" ) != -1 );

    // When the shell is disposed of, no removeActivateListener must be rendered
    // Important when disposing of a shell with ShellListener#shellClosed
    labelLCA.preserveValues( label );
    ActivateEvent.addListener( label, listener );
    shell.dispose();
    ControlLCAUtil.writeActivateListener( label );
    Fixture.fakeResponseWriter();
    markup = Fixture.getAllMarkup();
    assertEquals( -1, markup.indexOf( "removeActivateListenerWidget" ) );
  }

  public void testProcessSelection() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final StringBuffer log = new StringBuffer();
    SelectionListener listener = new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( WIDGET_SELECTED );
      }
      public void widgetDefaultSelected( SelectionEvent e ) {
        log.append( WIDGET_DEFAULT_SELECTED );
      }
    };
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( listener );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );

    // Test that requestParams like '...events.widgetSelected=w3' cause the
    // event to be fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( WIDGET_SELECTED, log.toString() );

    // Test that if requestParam '...events.widgetSelected' is null, no event
    // gets fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, null );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "", log.toString() );

    // Test that requestParams like '...events.widgetDefaultSelected=w3' cause
    // the event to be fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, buttonId );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( WIDGET_DEFAULT_SELECTED, log.toString() );

    // Test that if requestParam '...events.widgetSelected' is null, no event
    // gets fired
    log.setLength( 0 );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, null );
    ControlLCAUtil.processSelection( button, null, true );
    assertEquals( "", log.toString() );
  }

  public void testMaxZOrder() {
    for( int i = 0; i < ControlLCAUtil.MAX_STATIC_ZORDER; i++ ) {
      new Button( shell, SWT.PUSH );
    }
    Control control = new Button( shell, SWT.PUSH );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    ControlLCAUtil.preserveValues( control );
    assertEquals( new Integer( 1 ), adapter.getPreserved( Props.Z_INDEX ) );
  }

  public void testWriteCursor() throws Exception {
    final Control control = new Button( shell, SWT.PUSH );
    AbstractWidgetLCA controlLCA = WidgetUtil.getLCA( control );
    Cursor cursor = display.getSystemCursor( SWT.CURSOR_HAND );
    Fixture.markInitialized( control );
    Fixture.preserveWidgets();
    control.setCursor( cursor );
    ControlLCAUtil.writeCursor( control );
    String expected = "w.setCursor( \"pointer\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != -1 );

    Fixture.fakeResponseWriter();
    controlLCA.preserveValues( control );
    ControlLCAUtil.writeCursor( control );
    assertEquals( "", Fixture.getAllMarkup() );

    control.setCursor( null );
    ControlLCAUtil.writeCursor( control );
    assertTrue( Fixture.getAllMarkup().indexOf( "w.resetCursor();" ) != -1 );
  }

  public void testWriteKeyEvents() throws IOException {
    final java.util.List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeKeyListener( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    } );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeKeyListener( shell );
    String expected
      = "var w = wm.findWidgetById( \"w2\" );"
      + "w.setUserData( \"keyListener\", true );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteTraverseEvents() throws IOException {
    final java.util.List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeTraverseListener( shell );
    assertEquals( "", Fixture.getAllMarkup() );
    shell.addListener( SWT.Traverse, new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    } );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.writeTraverseListener( shell );
    String expected
      = "var w = wm.findWidgetById( \"w2\" );"
      + "w.setUserData( \"traverseListener\", true );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testProcessKeyEvents() {
    final java.util.List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    shell.addListener( SWT.KeyDown, new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    } );
    String shellId = WidgetUtil.getId( shell );
    // Simulate requests that carry information about a key-down event
    // - incomplete request
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    eventLog.clear();
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    try {
      ControlLCAUtil.processKeyEvents( shell );
      fail( "Attempting to process incomplete key-event-request must fail" );
    } catch( RuntimeException e ) {
      // expected
    }
    assertTrue( eventLog.isEmpty() );
    // - key-event without meaningful information (e.g. Shift-key only)
    Fixture.fakeNewRequest();
    Fixture.fakePhase( PhaseId.READ_DATA );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    ControlLCAUtil.processKeyEvents( shell );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    Event event = eventLog.get( 0 );
    assertEquals( SWT.KeyDown, event.type );
    assertEquals( shell, event.widget );
    assertEquals( 0, event.character );
    assertEquals( 0, event.keyCode );
    assertTrue( event.doit );
  }

  public void testProcessKeyEventsWithDoItFlag() {
    RWTFactory.getPhaseListenerRegistry().add( new CurrentPhase.Listener() );
    final java.util.List<Event> eventLog = new ArrayList<Event>();
    Listener doitTrueListener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    };
    Listener doitFalseListener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
        event.doit = false;
      }
    };
    Listener keyUpListener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    };
    shell.open();
    String shellId = WidgetUtil.getId( shell );

    // Simulate KeyEvent request, listener leaves doit untouched (doit==true)
    shell.addListener( SWT.KeyDown, doitTrueListener );
    shell.addListener( SWT.KeyUp, keyUpListener );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "65" );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 2, eventLog.size() );
    assertEquals( SWT.KeyDown, eventLog.get( 0 ).type );
    assertTrue( eventLog.get( 0 ).doit );
    assertEquals( SWT.KeyUp, eventLog.get( 1 ).type );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( ControlLCAUtil.JSFUNC_CANCEL_EVENT ) == -1 );
    assertTrue( markup.indexOf( ControlLCAUtil.JSFUNC_ALLOW_EVENT ) != -1 );
    shell.removeListener( SWT.KeyDown, doitTrueListener );

    // Simulate KeyEvent request, listener sets doit = false
    eventLog.clear();
    shell.addListener( SWT.KeyDown, doitFalseListener );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "0" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "65" );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 1, eventLog.size() );
    assertEquals( SWT.KeyDown, eventLog.get( 0 ).type );
    assertFalse( eventLog.get( 0 ).doit );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( ControlLCAUtil.JSFUNC_CANCEL_EVENT ) != -1 );
    assertTrue( markup.indexOf( ControlLCAUtil.JSFUNC_ALLOW_EVENT ) == -1 );
    shell.removeListener( SWT.KeyDown, doitFalseListener );
  }

  public void testProcessTraverseEventsWithDoItFlag() {
    RWTFactory.getPhaseListenerRegistry().add( new CurrentPhase.Listener() );
    final java.util.List<Event> eventLog = new ArrayList<Event>();
    Listener doitTrueListener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    };
    Listener doitFalseListener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
        event.doit = false;
      }
    };
    shell.open();
    String shellId = WidgetUtil.getId( shell );

    // Simulate Tab key stroke, listener leaves doit untouched (doit==true)
    shell.addListener( SWT.Traverse, doitTrueListener );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "9" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 1, eventLog.size() );
    assertEquals( SWT.Traverse, eventLog.get( 0 ).type );
    assertTrue( eventLog.get( 0 ).doit );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( ControlLCAUtil.JSFUNC_CANCEL_EVENT ) == -1 );
    shell.removeListener( SWT.Traverse, doitTrueListener );

    // Simulate Tab key stroke, listener sets doit = false
    eventLog.clear();
    shell.addListener( SWT.Traverse, doitFalseListener );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "9" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    Fixture.executeLifeCycleFromServerThread();
    assertEquals( 1, eventLog.size() );
    assertEquals( SWT.Traverse, eventLog.get( 0 ).type );
    assertFalse( eventLog.get( 0 ).doit );
    markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( ControlLCAUtil.JSFUNC_CANCEL_EVENT ) != -1 );
    shell.removeListener( SWT.Traverse, doitFalseListener );
  }

  public void testKeyAndTraverseEvents() {
    RWTFactory.getPhaseListenerRegistry().add( new CurrentPhase.Listener() );
    final java.util.List<Event> eventLog = new ArrayList<Event>();
    shell.open();
    String shellId = WidgetUtil.getId( shell );

    // Ensure that if a key event that notifies about a traversal key is
    // canceled (doit=false) the following traverse event isn't fired at all
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        eventLog.add( event );
      }
    };
    shell.addListener( SWT.Traverse, listener );
    shell.addListener( SWT.KeyDown, listener );
    shell.addListener( SWT.KeyUp, listener );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN, shellId );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_MODIFIER, "" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_KEY_CODE, "27" );
    Fixture.fakeRequestParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE, "0" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 3, eventLog.size() );
    Event traverseEvent = eventLog.get( 0 );
    assertEquals( SWT.Traverse, traverseEvent.type );
    assertEquals( SWT.TRAVERSE_ESCAPE, traverseEvent.detail );
    assertTrue( traverseEvent.doit );
    Event downEvent = eventLog.get( 1 );
    assertEquals( SWT.KeyDown, downEvent.type );
    Event upEvent = eventLog.get( 2 );
    assertEquals( SWT.KeyUp, upEvent.type );
  }

  public void testGetTraverseKey() {
    int traverseKey;
    traverseKey = ControlLCAUtil.getTraverseKey( 13, 0 );
    assertEquals( traverseKey, SWT.TRAVERSE_RETURN );
    traverseKey = ControlLCAUtil.getTraverseKey( 27, 0 );
    assertEquals( traverseKey, SWT.TRAVERSE_ESCAPE );
    traverseKey = ControlLCAUtil.getTraverseKey( 9, 0 );
    assertEquals( traverseKey, SWT.TRAVERSE_TAB_NEXT );
    traverseKey = ControlLCAUtil.getTraverseKey( 9, SWT.SHIFT );
    assertEquals( traverseKey, SWT.TRAVERSE_TAB_PREVIOUS );
    traverseKey = ControlLCAUtil.getTraverseKey( 9, SWT.SHIFT | SWT.CTRL );
    assertEquals( traverseKey, SWT.TRAVERSE_NONE );
  }

  public void testTranslateKeyCode() {
    int keyCode;
    keyCode = ControlLCAUtil.translateKeyCode( 40 );
    assertEquals( SWT.ARROW_DOWN, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 37 );
    assertEquals( SWT.ARROW_LEFT, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 38 );
    assertEquals( SWT.ARROW_UP, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 39 );
    assertEquals( SWT.ARROW_RIGHT, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 20 );
    assertEquals( SWT.CAPS_LOCK, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 36 );
    assertEquals( SWT.HOME, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 115 );
    assertEquals( SWT.F4, keyCode );
    keyCode = ControlLCAUtil.translateKeyCode( 123 );
    assertEquals( SWT.F12, keyCode );
  }

  public void testWriteBackgroundImage() throws IOException {
    Control control = new Button( shell, SWT.PUSH );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.preserveBackgroundImage( control );
    Fixture.markInitialized( control );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    control.setBackgroundImage( image );
    ControlLCAUtil.writeBackgroundImage( control );
    String imageLocation = ImageFactory.getImagePath( image );
    String expected =   "var w = wm.findWidgetById( \"w2\" );"
                      + "w.setUserData( \"backgroundImageSize\", [58,12 ] );"
                      + "w.setBackgroundImage( \""
                      + imageLocation
                      + "\" );";
    assertEquals( expected, Fixture.getAllMarkup() );

    Fixture.fakeResponseWriter();
    ControlLCAUtil.preserveBackgroundImage( control );
    control.setBackgroundImage( null );
    ControlLCAUtil.writeBackgroundImage( control );
    expected =   "w.setUserData( \"backgroundImageSize\", null );"
               + "w.resetBackgroundImage();";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testProcessHelpEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List<HelpEvent> log = new ArrayList<HelpEvent>();
    shell.addHelpListener( new HelpListener() {
      public void helpRequested( final HelpEvent event ) {
        log.add( event );
      }
    } );
    Fixture.fakeRequestParam( JSConst.EVENT_HELP, WidgetUtil.getId( shell ) );
    WidgetLCAUtil.processHelp( shell );
    assertEquals( 1, log.size() );
    HelpEvent event = log.get( 0 );
    assertSame( shell, event.widget );
    assertSame( display, event.display );
  }

  public void testWriteMouseListener() throws IOException {
    Composite control = new Composite( shell, SWT.NONE );
    CompositeLCA lca = new CompositeLCA();
    Fixture.fakeResponseWriter();
    lca.preserveValues( control );
    Fixture.markInitialized( control );

    control.addMouseListener( new MouseAdapter() {} );
    lca.renderChanges( control );

    String expected = "wm.setHasListener( wm.findWidgetById( \"w2\" ), \"mouse\", true );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteFocusListener_FocusableControl() throws IOException {
    Button control = new Button( shell, SWT.PUSH );
    ButtonLCA lca = new ButtonLCA();
    Fixture.fakeResponseWriter();
    lca.preserveValues( control );
    Fixture.markInitialized( control );

    control.addFocusListener( new FocusAdapter() {} );
    lca.renderChanges( control );

    String expected = "wm.setHasListener( wm.findWidgetById( \"w2\" ), \"focus\", true );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  public void testWriteFocusListener_NotFocusableControl() throws IOException {
    Label control = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();
    Fixture.fakeResponseWriter();
    lca.preserveValues( control );
    Fixture.markInitialized( control );

    control.addFocusListener( new FocusAdapter() {} );
    lca.renderChanges( control );

    assertEquals( "", Fixture.getAllMarkup() );
  }

  public void testWriteMenuDetectListener() throws IOException {
    Composite control = new Composite( shell, SWT.NONE );
    CompositeLCA lca = new CompositeLCA();
    Fixture.fakeResponseWriter();
    lca.preserveValues( control );
    Fixture.markInitialized( control );

    control.addMenuDetectListener( new MenuDetectListener() {
      public void menuDetected( MenuDetectEvent e ) {
      }
    } );
    lca.renderChanges( control );

    String expected = "wm.setHasListener( wm.findWidgetById( \"w2\" ), \"menuDetect\", true );";
    assertEquals( expected, Fixture.getAllMarkup() );
  }

  //////////////////////////////////////////////
  // Tests for new render methods using protocol

  public void testRenderVisibilityIntiallyFalse() throws IOException {
    control.setVisible( false );
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( control, "visibility" ) );
  }

  public void testRenderVisibilityInitiallyTrue() throws IOException {
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "visibility" ) );
  }

  public void testRenderVisibilityUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setVisible( false );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderVisible( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "visibility" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderBoundsIntiallyZero() throws IOException, JSONException {
    control = new Button( shell, SWT.PUSH );
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( control, "bounds" );
    assertEquals( 4, bounds.length() );
    assertEquals( 0, bounds.getInt( 0 ) );
    assertEquals( 0, bounds.getInt( 1 ) );
    assertEquals( 0, bounds.getInt( 2 ) );
    assertEquals( 0, bounds.getInt( 3 ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderBoundsInitiallySet() throws IOException, JSONException {
    control.setBounds( 10, 20, 100, 200 );
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( control, "bounds" );
    assertEquals( 4, bounds.length() );
    assertEquals( 10, bounds.getInt( 0 ) );
    assertEquals( 20, bounds.getInt( 1 ) );
    assertEquals( 100, bounds.getInt( 2 ) );
    assertEquals( 200, bounds.getInt( 3 ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderBoundsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setBounds( 10, 20, 100, 200 );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderBounds( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "bounds" ) );
  }

  public void testRenderIntialZIndex() throws IOException {
    ControlLCAUtil.renderZIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 300 ), message.findSetProperty( control, "zIndex" ) );
  }

  public void testRenderZIndex() throws IOException {
    control.moveBelow( new Button( shell, SWT.PUSH ) );
    ControlLCAUtil.renderZIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 299 ), message.findSetProperty( control, "zIndex" ) );
  }

  public void testRenderZIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.moveBelow( new Button( shell, SWT.PUSH ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderZIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "zIndex" ) );
  }

  public void testRenderIntialTabIndex() throws IOException {
    ShellLCA shellLCA = new ShellLCA();
    shellLCA.renderChanges( shell );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.renderTabIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 1 ), message.findSetProperty( control, "tabIndex" ) );
  }

  public void testRenderTabIndex() throws IOException {
    shell.setTabList( new Control[]{ new Button( shell, SWT.PUSH ), control } );
    ShellLCA shellLCA = new ShellLCA();
    shellLCA.renderChanges( shell );
    Fixture.fakeResponseWriter();
    ControlLCAUtil.renderTabIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( control, "tabIndex" ) );
  }

  public void testRenderTabIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.moveBelow( new Button( shell, SWT.PUSH ) );
    ShellLCA shellLCA = new ShellLCA();
    shellLCA.renderChanges( shell );
    Fixture.fakeResponseWriter();

    Fixture.preserveWidgets();
    ControlLCAUtil.renderTabIndex( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "tabIndex" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderIntialToolTip() throws IOException {
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderToolTip() throws IOException {
    control.setToolTipText( "foo" );
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderToolTipUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setToolTipText( "foo" );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderToolTip( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "toolTip" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderIntialMenu() throws IOException {
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderMenu() throws IOException {
    control.setMenu( new Menu( shell ) );
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    String expected = WidgetUtil.getId( control.getMenu() );
    assertEquals( expected, message.findSetProperty( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderMenuUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setMenu( new Menu( shell ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderMenu( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "menu" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderIntialEnabled() throws IOException {
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "enabled" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderEnabled() throws IOException {
    control.setEnabled( false );
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( control, "enabled" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderEnabledUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setEnabled( false );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderEnabled( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "enabled" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderIntialForeground() throws IOException {
    ControlLCAUtil.renderForeground( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "foreground" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderForeground() throws IOException {
    control.setForeground( new Color( display, 0, 16, 255 ) );
    ControlLCAUtil.renderForeground( control );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "#0010ff", message.findSetProperty( control, "foreground" ) );
  }

  // TODO [tb] : Move to WidgetLCAUtil_Test?
  public void testRenderForegroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setForeground( new Color( display, 0, 16, 255 ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderForeground( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "foreground" ) );
  }

  public void testRenderIntialBackgroundImage() throws IOException {
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "backgroundImage" ) );
  }

  public void testRenderBackgroundImage() throws IOException, JSONException {
    Image image = Graphics.getImage( Fixture.IMAGE1 );

    control.setBackgroundImage( image );
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JSONArray args = ( JSONArray )message.findSetProperty( control, "backgroundImage" );
    assertEquals( 3, args.length() );
    assertEquals( imageLocation, args.getString( 0 ) );
    assertEquals( 58, args.getInt( 1 ) );
    assertEquals( 12, args.getInt( 2 ) );
  }

  public void testRenderBackgroundImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setBackgroundImage( Graphics.getImage( Fixture.IMAGE1 ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderBackgroundImage( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "backgroundImage" ) );
  }

  public void testRenderInitialFont() throws IOException {
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "font" ) );
  }

  public void testRenderFont() throws IOException, JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( 4, result.length() );
    assertEquals( "Arial", ( ( JSONArray )result.get( 0 ) ).getString( 0 ) );
    assertEquals( 12, result.getInt( 1 ) );
    assertEquals( false, result.getBoolean( 2 ) );
    assertEquals( false, result.getBoolean( 3 ) );
  }
  
  public void testRenderFontBold() throws IOException, JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.BOLD ) );
    ControlLCAUtil.renderFont( control );
    
    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( true, result.getBoolean( 2 ) );
    assertEquals( false, result.getBoolean( 3 ) );
  }
  
  public void testRenderFontItalic() throws IOException, JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.ITALIC ) );
    ControlLCAUtil.renderFont( control );
    
    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( false, result.getBoolean( 2 ) );
    assertEquals( true, result.getBoolean( 3 ) );
  }
  
  public void testRenderFontItalicAndBold() throws IOException, JSONException {
    control.setFont( new Font( display, "Arial", 12, SWT.ITALIC | SWT.BOLD ) );
    ControlLCAUtil.renderFont( control );
    
    Message message = Fixture.getProtocolMessage();
    JSONArray result = ( JSONArray )message.findSetProperty( control, "font" );
    assertEquals( true, result.getBoolean( 2 ) );
    assertEquals( true, result.getBoolean( 3 ) );
  }

  public void testRenderFontUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );

    Fixture.preserveWidgets();
    ControlLCAUtil.renderFont( control );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( control, "font" ) );
  }
  
  public void testResetFont() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( control );
    control.setFont( new Font( display, "Arial", 12, SWT.NORMAL ) );
    
    Fixture.preserveWidgets();
    control.setFont( null );
    ControlLCAUtil.renderFont( control );
    
    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( control, "font" ) );
  }

}
