/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.expandbarkit;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;

public class ExpandBarLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private ExpandBar expandBar;
  private ExpandBarLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    expandBar = new ExpandBar( shell, SWT.NONE );
    lca = new ExpandBarLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    expandBar.setSize( expandBar.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    Fixture.markInitialized( display );
    // control: enabled
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    expandBar.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    expandBar.setEnabled( true );
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    expandBar.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( expandBar );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    expandBar.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    expandBar.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    expandBar.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    expandBar.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( expandBar );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( expandBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertEquals( "rwt.widgets.ExpandBar", operation.getType() );
  }

  public void testRenderCreateWithVScroll() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );

    lca.render( expandBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertEquals( "rwt.widgets.ExpandBar", operation.getType() );
    List<Object> styles = Arrays.asList( operation.getStyles() );
    assertFalse( styles.contains( "V_SCROLL" ) );
    assertEquals( Boolean.TRUE, message.findCreateProperty( expandBar, "vScrollBarVisible" ) );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( expandBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertEquals( WidgetUtil.getId( expandBar.getParent() ), operation.getParent() );
  }

  public void testRenderDispose() throws IOException {
    lca.renderDispose( expandBar );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( expandBar ), operation.getTarget() );
  }

  public void testRenderInitialBottomSpacingBounds() throws IOException, JSONException {
    lca.render( expandBar );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findCreateProperty( expandBar, "bottomSpacingBounds" );
    assertTrue( bounds.getInt( 2 ) > 0 );
    assertTrue( bounds.getInt( 3 ) > 0 );
  }

  public void testRenderBottomSpacingBounds() throws IOException, JSONException {
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    JSONArray bounds = ( JSONArray )message.findSetProperty( expandBar, "bottomSpacingBounds" );
    assertTrue( bounds.getInt( 2 ) > 0 );
    assertTrue( bounds.getInt( 3 ) > 0 );
  }

  public void testRenderBottomSpacingBoundsUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandBar );

    Fixture.preserveWidgets();
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandBar, "boubottomSpacingBoundsnds" ) );
  }

  public void testRenderInitialVScrollBarVisible() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    lca.render( expandBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertTrue( operation.getPropertyNames().indexOf( "vScrollBarVisible" ) == -1 );
  }

  public void testRenderVScrollBarVisible() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    new ExpandItem( expandBar, SWT.NONE );
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( expandBar, "vScrollBarVisible" ) );
  }

  public void testRenderVScrollBarVisibleUnchanged() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandBar );

    new ExpandItem( expandBar, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandBar, "vScrollBarVisible" ) );
  }

  public void testRenderInitialVScrollBarMax() throws IOException {
    expandBar = new ExpandBar( shell, SWT.NONE );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    lca.render( expandBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( expandBar );
    assertTrue( operation.getPropertyNames().indexOf( "vScrollBarMax" ) == -1 );
  }

  public void testRenderVScrollBarMax() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );

    new ExpandItem( expandBar, SWT.NONE );
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetProperty( expandBar, "vScrollBarMax" ) );
  }

  public void testRenderVScrollBarMaxUnchanged() throws IOException {
    expandBar = new ExpandBar( shell, SWT.V_SCROLL );
    expandBar.setSize( 100, 40 );
    new ExpandItem( expandBar, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( expandBar );

    new ExpandItem( expandBar, SWT.NONE );
    Fixture.preserveWidgets();
    lca.renderChanges( expandBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( expandBar, "vScrollBarMax" ) );
  }
}
