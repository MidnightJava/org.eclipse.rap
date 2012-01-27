/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.testfixture.*;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.internal.widgets.FormsControlLCA_AbstractTest;
import org.eclipse.ui.forms.internal.widgets.IHyperlinkAdapter;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.json.JSONArray;
import org.json.JSONException;

@SuppressWarnings("restriction")
public class HyperlinkLCA_Test extends FormsControlLCA_AbstractTest {

  private Display display;
  private Shell shell;
  private Hyperlink hyperlink;
  private HyperlinkLCA lca;

  protected void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    hyperlink = new Hyperlink( shell, SWT.NONE );
    lca = new HyperlinkLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  public void testSelectionEvent() {
    Hyperlink hyperlink = new Hyperlink( shell, SWT.NONE );
    testDefaultSelectionEvent( hyperlink );
  }

  private void testDefaultSelectionEvent( final Hyperlink hyperlink ) {
    final StringBuffer log = new StringBuffer();
    Listener listener = new Listener() {
      private static final long serialVersionUID = 1L;
      public void handleEvent( Event event ) {
        assertEquals( hyperlink, event.widget );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetDefaultSelected" );
      }
    };
    hyperlink.addListener( SWT.DefaultSelection, listener );
    String hyperlinkId = WidgetUtil.getId( hyperlink );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, hyperlinkId );
    Fixture.readDataAndProcessAction( hyperlink );
    assertEquals( "widgetDefaultSelected", log.toString() );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertEquals( "forms.widgets.Hyperlink", operation.getType() );
  }

  public void testRenderCreateWithWrap() throws IOException {
    hyperlink = new Hyperlink( shell, SWT.WRAP );

    lca.renderInitialization( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertEquals( "forms.widgets.Hyperlink", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertEquals( WidgetUtil.getId( hyperlink.getParent() ), operation.getParent() );
  }

  public void testRenderInitialText() throws IOException {
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "text" ) );
  }

  public void testRenderText() throws IOException {
    hyperlink.setText( "test" );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( hyperlink, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    hyperlink.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "text" ) );
  }

  public void testRenderInitialUnderlined() throws IOException {
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlined" ) );
  }

  public void testRenderUnderlined() throws IOException {
    hyperlink.setUnderlined( true );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( hyperlink, "underlined" ) );
  }

  public void testRenderUnderlinedUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    hyperlink.setUnderlined( true );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlined" ) );
  }

  public void testRenderInitialUnderlineMode() throws IOException {
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlineMode" ) );
  }

  public void testRenderUnderlineMode() throws IOException {
    getAdapter( hyperlink ).setUnderlineMode( HyperlinkSettings.UNDERLINE_HOVER );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( hyperlink, "underlineMode" ) );
  }

  public void testRenderUnderlineModeUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    getAdapter( hyperlink ).setUnderlineMode( HyperlinkSettings.UNDERLINE_HOVER );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "underlineMode" ) );
  }

  public void testRenderInitialActiveBackground() throws IOException {
    lca.render( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertTrue( operation.getPropertyNames().indexOf( "activeBackground" ) == -1 );
  }

  public void testRenderActiveBackground() throws IOException, JSONException {
    getAdapter( hyperlink ).setActiveBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( hyperlink, "activeBackground" );
    assertEquals( 0, actual.getInt( 0 ) );
    assertEquals( 255, actual.getInt( 1 ) );
    assertEquals( 0, actual.getInt( 2 ) );
    assertEquals( 255, actual.getInt( 3 ) );
  }

  public void testRenderActiveBackgroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    getAdapter( hyperlink ).setActiveBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "activeBackground" ) );
  }

  public void testRenderInitialActiveForeground() throws IOException {
    lca.render( hyperlink );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( hyperlink );
    assertTrue( operation.getPropertyNames().indexOf( "activeForeground" ) == -1 );
  }

  public void testRenderActiveForeground() throws IOException, JSONException {
    getAdapter( hyperlink ).setActiveForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    JSONArray actual = ( JSONArray )message.findSetProperty( hyperlink, "activeForeground" );
    assertEquals( 0, actual.getInt( 0 ) );
    assertEquals( 255, actual.getInt( 1 ) );
    assertEquals( 0, actual.getInt( 2 ) );
    assertEquals( 255, actual.getInt( 3 ) );
  }

  public void testRenderActiveForegroundUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );

    getAdapter( hyperlink ).setActiveForeground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( hyperlink, "activeForeground" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( hyperlink, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = new Listener() {
      private static final long serialVersionUID = 1L;
      public void handleEvent( Event event ) {
      }
    };
    hyperlink.addListener( SWT.DefaultSelection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );
    Fixture.preserveWidgets();

    hyperlink.removeListener( SWT.DefaultSelection, listener );
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( hyperlink, "selection" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( hyperlink );
    Fixture.preserveWidgets();

    hyperlink.addListener( SWT.DefaultSelection, new Listener() {
      private static final long serialVersionUID = 1L;
      public void handleEvent( Event event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( hyperlink );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( hyperlink, "selection" ) );
  }

  private IHyperlinkAdapter getAdapter( Hyperlink hyperlink ) {
    return ( IHyperlinkAdapter )hyperlink.getAdapter( IHyperlinkAdapter.class );
  }

}
