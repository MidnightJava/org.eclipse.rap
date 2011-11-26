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
package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.*;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateAdapter;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.*;


// TODO [rst] Split into different test classes for button types
@SuppressWarnings("deprecation")
public class ButtonLCA_Test extends TestCase {

  private static final String PROP_SELECTION_LISTENER = "listener_selection";

  private Display display;
  private Shell shell;
  private ButtonLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new ButtonLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testRadioPreserveValues() {
    Button button = new Button( shell, SWT.RADIO );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
  }

  public void testCheckPreserveValues() {
    Button button = new Button( shell, SWT.CHECK );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    button.setGrayed( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
    assertEquals( Boolean.TRUE, adapter.getPreserved( ButtonLCAUtil.PROP_GRAYED ) );
  }

  public void testTogglePreserveValues() {
    Button button = new Button( shell, SWT.TOGGLE );
    Fixture.markInitialized( display );
    testPreserveValues( display, button );
    button.setSelection( true );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( ButtonLCAUtil.PROP_SELECTION ) );
  }

  private void testPreserveValues( final Display display, final Button button ) {
    Boolean hasListeners;
    // Text,Image
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    if( ( button.getStyle() & SWT.ARROW ) == 0 ) {
      button.setText( "abc" );
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      Object object = adapter.getPreserved( Props.TEXT );
      assertEquals( "abc", ( String )object );
      Fixture.clearPreserved();
      Image image = Graphics.getImage( Fixture.IMAGE1 );
      button.setImage( image );
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      assertSame( image, adapter.getPreserved( Props.IMAGE ) );
      Fixture.clearPreserved();
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
      assertEquals( Boolean.FALSE, hasListeners );
      Fixture.clearPreserved();
      button.addFocusListener( new FocusAdapter() { } );
      Fixture.preserveWidgets();
      adapter = WidgetUtil.getAdapter( button );
      hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
      assertEquals( Boolean.TRUE, hasListeners );
      Fixture.clearPreserved();
    }
    //Selection_Listener
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( PROP_SELECTION_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    button.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( PROP_SELECTION_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    button.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( button );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    button.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    button.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    button.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    button.setEnabled( true );
    //control_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    button.addControlListener( new ControlAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    button.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    button.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    button.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( null, button.getToolTipText() );
    Fixture.clearPreserved();
    button.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    assertEquals( "some text", button.getToolTipText() );
    Fixture.clearPreserved();
    //activate_listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( button, new ActivateAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( button );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
  }

  public void testDisabledButtonSelection() {
    final StringBuffer log = new StringBuffer();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final Button button = new Button( shell, SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    ActivateEvent.addListener( button, new ActivateAdapter() {
      public void activated( ActivateEvent event ) {
        log.append( "widgetActivated|" );
        button.setEnabled( false );
      }
    } );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.append( "widgetSelected|" );
      }
    } );
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( label );
    Fixture.fakeNewRequest( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_ACTIVATED, buttonId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( "widgetActivated|", log.toString() );
  }

  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    final Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertSame( button, event.getSource() );
        assertEquals( 0, event.detail );
        log.append( "widgetSelected" );
      }
    } );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( "org.eclipse.swt.events.widgetSelected", buttonId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( "widgetSelected", log.toString() );
  }

  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=224872
  public void testRadioSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    final Button button1 = new Button( shell, SWT.RADIO );
    button1.setText( "1" );
    final Button button2 = new Button( shell, SWT.RADIO );
    button2.setText( "2" );
    final Button button3 = new Button( shell, SWT.RADIO );
    button3.setText( "3" );
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        Button button = ( Button )event.getSource();
        log.append( button.getText() );
        log.append( ":" );
        log.append( button.getSelection() );
        log.append( "|" );
      }
    };
    button1.addSelectionListener( listener );
    button2.addSelectionListener( listener );
    button3.addSelectionListener( listener );
    String button1Id = WidgetUtil.getId( button1 );
    String button2Id = WidgetUtil.getId( button2 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( button1Id + ".selection", "true" );
    Fixture.readDataAndProcessAction( display );
    assertTrue( log.indexOf( "1:true" ) != -1 );
    assertTrue( log.indexOf( "2:" ) == -1 );
    assertTrue( log.indexOf( "3:" ) == -1 );

    log.delete( 0, log.length() );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( button1Id + ".selection", "false" );
    Fixture.fakeRequestParam( button2Id + ".selection", "true" );
    Fixture.readDataAndProcessAction( display );
    assertTrue( log.indexOf( "1:false" ) != -1 );
    assertTrue( log.indexOf( "2:true" ) != -1 );
    assertTrue( log.indexOf( "3:" ) == -1 );
  }

  public void testRadioTypedSelectionEventOrder() {
    final java.util.List<SelectionEvent> log = new ArrayList<SelectionEvent>();
    Button button1 = new Button( shell, SWT.RADIO );
    button1.setText( "1" );
    Button button2 = new Button( shell, SWT.RADIO );
    button2.setText( "2" );
    SelectionAdapter listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( event );
      }
    };
    button1.addSelectionListener( listener );
    button2.addSelectionListener( listener );
    button2.setSelection( true );
    String button1Id = WidgetUtil.getId( button1 );
    String button2Id = WidgetUtil.getId( button2 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( button1Id + ".selection", "true" );
    Fixture.fakeRequestParam( button2Id + ".selection", "false" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    assertSame( button2, log.get( 0 ).widget );
    assertSame( button1, log.get( 1 ).widget );
  }

  public void testRadioUntypedSelectionEventOrder() {
    final java.util.List<Event> log = new ArrayList<Event>();
    Button button1 = new Button( shell, SWT.RADIO );
    button1.setText( "1" );
    Button button2 = new Button( shell, SWT.RADIO );
    button2.setText( "2" );
    Listener listener = new Listener() {
      public void handleEvent( final Event event ) {
        log.add( event );
      }
    };
    button1.addListener( SWT.Selection, listener );
    button2.addListener( SWT.Selection, listener );
    button2.setSelection( true );
    String button1Id = WidgetUtil.getId( button1 );
    String button2Id = WidgetUtil.getId( button2 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( button1Id + ".selection", "true" );
    Fixture.fakeRequestParam( button2Id + ".selection", "false" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( 2, log.size() );
    Event event = log.get( 0 );
    assertSame( button2, event.widget );
    event = log.get( 1 );
    assertSame( button1, event.widget );
  }

  public void testRenderWrap() throws Exception {
    Button button = new Button( shell, SWT.PUSH | SWT.WRAP );
    Fixture.fakeResponseWriter();
    PushButtonDelegateLCA lca = new PushButtonDelegateLCA();

    lca.renderInitialization( button );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( button );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
  }

  public void testRenderCreate() throws IOException {
    Button pushButton = new Button( shell, SWT.PUSH );

    lca.renderInitialization( pushButton );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( pushButton );
    assertEquals( "rwt.widgets.Button", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "PUSH" ) );
  }

  public void testRenderParent() throws IOException {
    Button pushButton = new Button( shell, SWT.PUSH );

    lca.renderInitialization( pushButton );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( pushButton );
    assertEquals( WidgetUtil.getId( pushButton.getParent() ), operation.getParent() );
  }

  public void testRenderInitialText() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "text" ) );
  }

  public void testRenderText() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    button.setText( "test" );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( button, "text" ) );
  }

  public void testRenderTextWithQuotationMarks() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    button.setText( "te\"s't" );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "te\"s't", message.findSetProperty( button, "text" ) );
  }

  public void testRenderTextWithNewlines() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    button.setText( "\ntes\r\nt\n" );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "\ntes\r\nt\n", message.findSetProperty( button, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );

    button.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "text" ) );
  }

  public void testRenderInitialAlignment() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "alignment" ) );
  }

  public void testRenderAlignment() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    button.setAlignment( SWT.RIGHT );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( button, "alignment" ) );
  }

  public void testRenderAlignmentUnchanged() throws IOException {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );

    button.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "alignment" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Fixture.preserveWidgets();

    button.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( button, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Button button = new Button( shell, SWT.PUSH );
    SelectionListener listener = new SelectionAdapter() { };
    button.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Fixture.preserveWidgets();

    button.removeSelectionListener( listener );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( button, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Fixture.preserveWidgets();

    button.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( button, "selection" ) );
  }

  public void testRenderInitialImage() throws IOException {
    Button button = new Button( shell, SWT.PUSH );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    Button button = new Button( shell, SWT.PUSH );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    button.setImage( image );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( button, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    button.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    Button button = new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    button.setImage( image );

    Fixture.preserveWidgets();
    button.setImage( null );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( button, "image" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    Button button = new Button( shell, SWT.CHECK );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "selection" ) );
  }

  public void testRenderSelection() throws IOException {
    Button button = new Button( shell, SWT.CHECK );

    button.setSelection( true );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( button, "selection" ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Button button = new Button( shell, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );

    button.setSelection( true );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "selection" ) );
  }

  public void testRenderInitialGrayed() throws IOException {
    Button button = new Button( shell, SWT.CHECK );

    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "grayed" ) );
  }

  public void testRenderGrayed() throws IOException {
    Button button = new Button( shell, SWT.CHECK );

    button.setGrayed( true );
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( button, "grayed" ) );
  }

  public void testRenderGrayedUnchanged() throws IOException {
    Button button = new Button( shell, SWT.CHECK );
    Fixture.markInitialized( display );
    Fixture.markInitialized( button );

    button.setGrayed( true );
    Fixture.preserveWidgets();
    lca.renderChanges( button );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( button, "grayed" ) );
  }
}
