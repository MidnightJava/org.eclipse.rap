/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.menukit;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.json.JSONObject;

public class MenuLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private MenuLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new MenuLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testUnassignedMenuBar() throws IOException {
    String shellId = WidgetUtil.getId( shell );
    Menu menuBar = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menuBar );
    // Ensure that a menuBar that is not assigned to any shell (via setMenuBar)
    // is rendered but without settings its parent
    lca.renderChanges( menuBar );
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menuBar, "parent" ) );
    // The contrary: an assigned menuBar has to be rendered with setParent
    Fixture.fakeNewRequest( display );
    Fixture.preserveWidgets();
    shell.setMenuBar( menuBar );
    lca.renderChanges( menuBar );
    message = Fixture.getProtocolMessage();
    assertEquals( shellId, message.findSetProperty( menuBar, "parent" ) );
    // Un-assigning a menuBar must result in setParent( null ) being rendered
    Fixture.fakeNewRequest( display );
    Fixture.preserveWidgets();
    shell.setMenuBar( null );
    lca.renderChanges( menuBar );
    message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( menuBar, "parent" ) );
  }

  public void testWriteBoundsForMenuBar() throws IOException {
    Menu menuBar = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( shell );
    Fixture.markInitialized( menuBar );
    // initial unassigned rendering -> no setSpace
    lca.renderChanges( menuBar );
    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menuBar, "bounds" ) );
    // initial assigned rendering -> no setSpace
    Fixture.fakeNewRequest( display );
    Fixture.preserveWidgets();
    shell.setMenuBar( menuBar );
    lca.renderChanges( menuBar );
    message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( menuBar, "bounds" ) );
    // changing bounds of shell -> an assigned menuBar must adjust its size
    Fixture.fakeNewRequest( display );
    Fixture.preserveWidgets();
    shell.setBounds( new Rectangle( 1, 2, 3, 4 ) );
    lca.renderChanges( menuBar );
    message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( menuBar, "bounds" ) );
    // changing bounds of shell -> an unassigned menuBar does nothing
    Fixture.fakeNewRequest( display );
    Fixture.preserveWidgets();
    shell.setMenuBar( null );
    Fixture.preserveWidgets();
    shell.setBounds( new Rectangle( 5, 6, 7, 8 ) );
    lca.renderChanges( menuBar );
    message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menuBar, "bounds" ) );
    // Simulate client-side size-change of shell: menuBar must render new size
    shell.setMenuBar( menuBar );
    String shellId = WidgetUtil.getId( shell );
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread( );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( shellId + ".bounds.x", "0" );
    Fixture.fakeRequestParam( shellId + ".bounds.y", "0" );
    Fixture.fakeRequestParam( shellId + ".bounds.width", "1234" );
    Fixture.fakeRequestParam( shellId + ".bounds.height", "4321" );
    Fixture.executeLifeCycleFromServerThread( );
    message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( menuBar, "bounds" ) );
  }

  public void testRenderCreate() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.renderInitialization( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "BAR" ) );
  }

  public void testRenderCreatePopUp() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    lca.renderInitialization( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "POP_UP" ) );
  }

  public void testRenderCreateDropDown() throws IOException {
    Menu menu = new Menu( shell, SWT.DROP_DOWN );

    lca.renderInitialization( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "DROP_DOWN" ) );
  }

  public void testRenderCreatePopUp_NoRadioGroup() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP | SWT.NO_RADIO_GROUP );

    lca.renderInitialization( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertEquals( "rwt.widgets.Menu", operation.getType() );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "POP_UP" ) );
    assertTrue( Arrays.asList( operation.getStyles() ).contains( "NO_RADIO_GROUP" ) );
  }

  public void testRenderDispose() throws IOException {
    Menu menu = new Menu( shell, SWT.POP_UP );

    lca.renderDispose( menu );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( menu ), operation.getTarget() );
  }

  public void testRenderInitialParent() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertTrue( operation.getPropertyNames().indexOf( "parent" ) == -1 );
  }

  public void testRenderParent() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    shell.setMenuBar( menu );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( WidgetUtil.getId( shell ), message.findSetProperty( menu, "parent" ) );
  }

  public void testRenderParentUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    shell.setMenuBar( menu );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "parent" ) );
  }

  public void testRenderInitialBounds() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertTrue( operation.getPropertyNames().indexOf( "bounds" ) == -1 );
  }

  public void testRenderBounds() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    shell.setMenuBar( menu );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetOperation( menu, "bounds" ) );
  }

  public void testRenderBoundsUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    shell.setMenuBar( menu );
    // Note: Menu bounds are preserved in ShellLCA#readData
    WidgetUtil.getLCA( shell ).readData( shell );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "bounds" ) );
  }

  public void testRenderInitialCustomVariant() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  public void testRenderCustomVariant() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    menu.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( menu, "customVariant" ) );
  }

  public void testRenderCustomVariantUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    menu.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "customVariant" ) );
  }

  public void testRenderInitialEnabled() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    lca.render( menu );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( menu );
    assertTrue( operation.getPropertyNames().indexOf( "enabled" ) == -1 );
  }

  public void testRenderEnabled() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );

    menu.setEnabled( false );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( menu, "enabled" ) );
  }

  public void testRenderEnabledUnchanged() throws IOException {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );

    menu.setEnabled( false );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( menu, "enabled" ) );
  }

  public void testRenderAddMenuListener() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addMenuListener( new MenuAdapter() {} );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( menu, "menu" ) );
  }

  public void testRenderAddMenuListener_ArmListener() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    MenuItem item = new MenuItem( menu, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    item.addArmListener( new ArmListener() {
      public void widgetArmed( ArmEvent e ) {
      }
    } );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( menu, "menu" ) );
  }

  public void testRenderRemoveMenuListener() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    MenuListener listener = new MenuAdapter() { };
    menu.addMenuListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.removeMenuListener( listener );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( menu, "menu" ) );
  }

  public void testRenderMenuListenerUnchanged() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addMenuListener( new MenuAdapter() {} );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "menu" ) );
  }

  public void testRenderAddHelpListener() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addHelpListener( new HelpListener() {
      public void helpRequested( HelpEvent e ) {
      }
    } );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( menu, "help" ) );
  }

  public void testRenderRemoveHelpListener() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    HelpListener listener = new HelpListener() {
      public void helpRequested( HelpEvent e ) {
      }
    };
    menu.addHelpListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.removeHelpListener( listener );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( menu, "help" ) );
  }

  public void testRenderHelpListenerUnchanged() throws Exception {
    Menu menu = new Menu( shell, SWT.BAR );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.addHelpListener( new HelpListener() {
      public void helpRequested( HelpEvent e ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( menu, "help" ) );
  }

  public void testRenderShowMenu() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    menu.setLocation( 1, 2 );
    menu.setVisible( true );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( menu, "showMenu" );
    assertEquals( Integer.valueOf( 1 ), operation.getProperty( "x" ) );
    assertEquals( Integer.valueOf( 2 ), operation.getProperty( "y" ) );
  }

  public void testRenderUnhideItems() throws Exception {
    Menu menu = new Menu( shell, SWT.POP_UP );
    new MenuItem( menu, SWT.PUSH );
    Fixture.markInitialized( display );
    Fixture.markInitialized( menu );
    Fixture.preserveWidgets();

    Fixture.fakeRequestParam( JSConst.EVENT_MENU_SHOWN, WidgetUtil.getId( menu ) );
    lca.renderChanges( menu );

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( menu, "unhideItems" );
    assertEquals( Boolean.TRUE, operation.getProperty( "reveal" ) );
  }
}
