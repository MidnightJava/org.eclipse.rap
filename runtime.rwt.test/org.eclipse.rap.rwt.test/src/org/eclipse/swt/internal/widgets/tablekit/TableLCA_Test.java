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
package org.eclipse.swt.internal.widgets.tablekit;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.*;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.tablekit.TableLCAUtil.ItemMetrics;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;


public class TableLCA_Test extends TestCase {

  private Display display;
  private Shell shell;

  public void testInitialization() throws Exception {
    Table table = new Table( shell, SWT.NONE );
    TableLCA lca = new TableLCA();
    Fixture.fakeResponseWriter();
    lca.renderInitialization( table );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "new org.eclipse.rwt.widgets.Tree( {" ) != -1 );
    assertTrue( markup.indexOf( "\"appearance\": \"table\"" ) != -1 );
    assertTrue( markup.indexOf( "\"fullSelection\": true" ) != -1 );
    assertTrue( markup.indexOf( "\"indentionWidth\": 0" ) != -1 );
    assertTrue( markup.indexOf( "w.setTreeColumn( -1 )" ) != -1 );
    assertFalse( markup.indexOf( "setHasNoScroll(" ) != -1 );
    assertFalse( markup.indexOf( "setHasCheckBoxes(" ) != -1 );
  }

  public void testInitializationWithNoScroll() throws Exception {
    Table table = new Table( shell, SWT.NO_SCROLL );
    TableLCA lca = new TableLCA();
    Fixture.fakeResponseWriter();
    lca.renderInitialization( table );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "\"noScroll\": true" ) != -1 );
  }

  public void testInitializationWithMultiSelection() throws Exception {
    Table table = new Table( shell, SWT.MULTI );
    TableLCA lca = new TableLCA();
    Fixture.fakeResponseWriter();
    lca.renderInitialization( table );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "\"multiSelection\": true" ) != -1 );
  }

  public void testInitializationWithHideSelection() throws Exception {
    Table table = new Table( shell, SWT.HIDE_SELECTION );
    TableLCA lca = new TableLCA();
    Fixture.fakeResponseWriter();
    lca.renderInitialization( table );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "\"hideSelection\": true" ) != -1 );
  }

  public void testInitializationWithVirtual() throws Exception {
    Table table = new Table( shell, SWT.VIRTUAL );
    TableLCA lca = new TableLCA();
    Fixture.fakeResponseWriter();
    lca.renderInitialization( table );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "\"virtual\": true" ) != -1 );
  }

  public void testInitializationWithCheck() throws Exception {
    Table table = new Table( shell, SWT.CHECK );
    TableLCA lca = new TableLCA();
    Fixture.fakeResponseWriter();
    lca.renderInitialization( table );
    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "\"check\": true" ) != -1 );
    // NOTE : checkbox left = ( checkbox width 21 - image width 15 ) / 2 = 3
    assertTrue( markup.indexOf( "\"checkBoxMetrics\": [ 3, 15" ) != -1 );
  }

  public void testInitializationWithFixedColumns() throws Exception {
    Table table = new Table( shell, SWT.NONE );
    table.setData( "fixedColumns", new Integer( 0 ) );
    TableLCA lca = new TableLCA();
    Fixture.fakeResponseWriter();

    lca.renderInitialization( table );

    String markup = Fixture.getAllMarkup();
    assertTrue( markup.indexOf( "\"splitContainer\": true" ) != -1 );
  }

  public void testPreserveValues() {
    Table table = new Table( shell, SWT.BORDER );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    Object headerHeight = adapter.getPreserved( TableLCA.PROP_HEADER_HEIGHT );
    assertEquals( new Integer( 0 ), headerHeight );
    Object headerVisible = adapter.getPreserved( TableLCA.PROP_HEADER_VISIBLE );
    assertEquals( Boolean.FALSE, headerVisible );
    Object linesVisible = adapter.getPreserved( TableLCA.PROP_LINES_VISIBLE );
    assertEquals( Boolean.FALSE, linesVisible );
    Object itemHeight = adapter.getPreserved( TableLCA.PROP_ITEM_HEIGHT );
    assertEquals( new Integer( table.getItemHeight() ), itemHeight );
    assertEquals( new Integer( 0 ),
                  adapter.getPreserved( TableLCA.PROP_ITEM_COUNT ) );
    Object topIndex = adapter.getPreserved( TableLCA.PROP_TOP_ITEM_INDEX );
    assertEquals( new Integer( table.getTopIndex() ), topIndex );
    Object focusIndex = adapter.getPreserved( TableLCAUtil.PROP_FOCUS_INDEX );
    assertEquals( new Integer( -1 ), focusIndex );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.FALSE, hasListeners );
    String prop = TableLCA.PROP_SCROLLBARS_SELECTION_LISTENER;
    hasListeners = ( Boolean )adapter.getPreserved( prop );
    assertEquals( Boolean.FALSE, hasListeners );
    ItemMetrics[] itemMetrics = ( ItemMetrics[] )adapter.getPreserved( TableLCAUtil.PROP_ITEM_METRICS );
    int imageLeft1 = ( TableLCAUtil.getItemMetrics( table )[ 0 ] ).imageLeft;
    assertEquals( imageLeft1, itemMetrics[ 0 ].imageLeft );
    int imageWidth = ( TableLCAUtil.getItemMetrics( table )[ 0 ] ).imageWidth;
    assertEquals( imageWidth, itemMetrics[ 0 ].imageWidth );
    int textLeft = ( TableLCAUtil.getItemMetrics( table )[ 0 ] ).textLeft;
    assertEquals( textLeft, itemMetrics[ 0 ].textLeft );
    int textWidth1 = ( TableLCAUtil.getItemMetrics( table )[ 0 ] ).textWidth;
    assertEquals( textWidth1, itemMetrics[ 0 ].textWidth );
    assertEquals( Boolean.FALSE, adapter.getPreserved( TableLCA.PROP_ALWAYS_HIDE_SELECTION ) );
    assertEquals( new Integer( 0 ), adapter.getPreserved( TableLCA.PROP_LEFT_OFFSET ) );
    Fixture.clearPreserved();
    TableColumn tc1 = new TableColumn( table, SWT.CENTER );
    tc1.setText( "column1" );
    tc1.setWidth( 50 );
    TableColumn tc2 = new TableColumn( table, SWT.CENTER );
    tc2.setText( "column2" );
    tc2.setWidth( 50 );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( 0, "item11" );
    item1.setText( 1, "item12" );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setText( 0, "item21" );
    item2.setText( 1, "item22" );
    table.setHeaderVisible( true );
    table.setLinesVisible( true );
    table.setTopIndex( 1 );
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    table.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    headerHeight = adapter.getPreserved( TableLCA.PROP_HEADER_HEIGHT );
    assertEquals( new Integer( table.getHeaderHeight() ), headerHeight );
    headerVisible = adapter.getPreserved( TableLCA.PROP_HEADER_VISIBLE );
    assertEquals( Boolean.TRUE, headerVisible );
    linesVisible = adapter.getPreserved( TableLCA.PROP_LINES_VISIBLE );
    assertEquals( Boolean.TRUE, linesVisible );
    itemHeight = adapter.getPreserved( TableLCA.PROP_ITEM_HEIGHT );
    assertEquals( new Integer( table.getItemHeight() ), itemHeight );
    assertEquals( new Integer( 2 ), adapter.getPreserved( TableLCA.PROP_ITEM_COUNT ) );
    assertEquals( new Integer( 1 ), adapter.getPreserved( TableLCA.PROP_TOP_ITEM_INDEX ) );
    hasListeners = ( Boolean )adapter.getPreserved( Props.SELECTION_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    itemMetrics = ( ItemMetrics[] )adapter.getPreserved( TableLCAUtil.PROP_ITEM_METRICS );
    imageLeft1 = ( TableLCAUtil.getItemMetrics( table )[ 0 ] ).imageLeft;
    assertEquals( imageLeft1, itemMetrics[ 0 ].imageLeft );
    imageWidth = ( TableLCAUtil.getItemMetrics( table )[ 0 ] ).imageWidth;
    assertEquals( imageWidth, itemMetrics[ 0 ].imageWidth );
    textLeft = ( TableLCAUtil.getItemMetrics( table )[ 0 ] ).textLeft;
    assertEquals( textLeft, itemMetrics[ 0 ].textLeft );
    textWidth1 = ( TableLCAUtil.getItemMetrics( table )[ 0 ] ).textWidth;
    assertEquals( textWidth1, itemMetrics[ 0 ].textWidth );
    Fixture.clearPreserved();
    // scrollbars selection listener
    SelectionAdapter listener = new SelectionAdapter() {
    };
    table.getHorizontalBar().addSelectionListener( listener );
    Fixture.preserveWidgets();
    hasListeners = ( Boolean )adapter.getPreserved( prop );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    table.getHorizontalBar().removeSelectionListener( listener );
    Fixture.preserveWidgets();
    hasListeners = ( Boolean )adapter.getPreserved( prop );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    table.getVerticalBar().addSelectionListener( listener );
    Fixture.preserveWidgets();
    hasListeners = ( Boolean )adapter.getPreserved( prop );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    table.getVerticalBar().removeSelectionListener( listener );
    Fixture.preserveWidgets();
    hasListeners = ( Boolean )adapter.getPreserved( prop );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    // control: enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    table.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    table.setEnabled( true );
    // visible
    table.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    table.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( table );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    table.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    // bounds
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    table.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // control_listeners (Table always registers a control listener)
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    hasListeners = ( Boolean )adapter.getPreserved( Props.CONTROL_LISTENERS );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    // z-index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    table.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    table.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    table.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    // tab_index
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertTrue( adapter.getPreserved( Props.Z_INDEX ) != null );
    Fixture.clearPreserved();
    // scroll bars
    Fixture.preserveWidgets();
    Object preserved = adapter.getPreserved( TableLCA.PROP_HAS_H_SCROLL_BAR );
    assertTrue( preserved != null );
    preserved = adapter.getPreserved( TableLCA.PROP_HAS_V_SCROLL_BAR );
    assertTrue( preserved != null );
    Fixture.clearPreserved();
    // tooltip text
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( null, table.getToolTipText() );
    Fixture.clearPreserved();
    table.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    assertEquals( "some text", table.getToolTipText() );
    Fixture.clearPreserved();
    // activate listeners, focus listeners
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    table.addFocusListener( new FocusAdapter() { } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    hasListeners = ( Boolean )adapter.getPreserved( Props.FOCUS_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    ActivateEvent.addListener( table, new ActivateAdapter() {
    } );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    hasListeners = ( Boolean )adapter.getPreserved( Props.ACTIVATE_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
  }

  public void testPreserveEnableCellToolTip() {
    Table table = new Table( shell, SWT.BORDER );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    Boolean preserved
      = ( Boolean )adapter.getPreserved( TableLCA.PROP_ENABLE_CELL_TOOLTIP );
    assertEquals( Boolean.FALSE, preserved );
    Fixture.clearPreserved();
    table.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( table );
    preserved
      = ( Boolean )adapter.getPreserved( TableLCA.PROP_ENABLE_CELL_TOOLTIP );
    assertEquals( Boolean.TRUE, preserved );
    Fixture.clearPreserved();
  }

  public void testWidgetSelectedWithCheck() {
    final SelectionEvent[] events = new SelectionEvent[ 1 ];
    Table table = new Table( shell, SWT.CHECK );
    TableItem item1 = new TableItem( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    table.setSelection( 0 );
    table.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        events[ 0 ] = event;
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
        fail( "unexpected event: widgetDefaultSelected" );
      }
    } );
    // Simulate request that comes in after item2 was checked (but not selected)
    Fixture.fakeNewRequest( display );
    String tableId = WidgetUtil.getId( table );
    String item2Id = WidgetUtil.getId( item2 );
    Fixture.fakeRequestParam( item2Id + ".checked", "true" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, item2Id);
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_DETAIL, "check" );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( item1, table.getSelection()[ 0 ] );
  }

  public void testWidgetDefaultSelected() {
    final SelectionEvent[] events = new SelectionEvent[ 1 ];
    Table table = new Table( shell, SWT.MULTI );
    TableItem item1 = new TableItem( table, SWT.NONE );
    TableItem item2 = new TableItem( table, SWT.NONE );
    table.setSelection( 0 );
    table.addSelectionListener( new SelectionListener() {
      public void widgetSelected( SelectionEvent event ) {
        fail( "unexpected event: widgetSelected" );
      }
      public void widgetDefaultSelected( SelectionEvent event ) {
        events[ 0 ] = event;
      }
    } );
    // Simulate request that comes in after item2 was checked (but not selected)
    Fixture.fakeNewRequest( display );
    String tableId = WidgetUtil.getId( table );
    String item2Id = WidgetUtil.getId( item2 );
    String itemParam = JSConst.EVENT_WIDGET_DEFAULT_SELECTED + ".item";
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, tableId );
    Fixture.fakeRequestParam( itemParam, item2Id );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( item1, table.getSelection()[ 0 ] );
    // Simulate request that comes when <Return> is pressed
    // with focused item is one of the selected
    events[ 0 ] = null;
    table.setSelection( 1 ); // Set focused item
    table.select( 0 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, tableId );
    Fixture.fakeRequestParam( itemParam, item2Id );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 2, table.getSelectionCount() );
    assertEquals( 1, table.getSelectionIndex() );
    // Simulate request that comes when <Return> is pressed
    // with focused item is not one of the selected
    events[ 0 ] = null;
    table.setSelection( 0 ); // Set focused item
    table.deselectAll();
    table.select( 1 );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, tableId );
    Fixture.fakeRequestParam( itemParam, item2Id );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 1, table.getSelectionCount() );
    assertEquals( 1, table.getSelectionIndex() );
    // Simulate request that comes when <Return> is pressed
    // and there is no selection
    events[ 0 ] = null;
    table.setSelection( 1 ); // Set focused item
    table.deselectAll();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_DEFAULT_SELECTED, tableId );
    Fixture.fakeRequestParam( itemParam, item2Id );
    Fixture.readDataAndProcessAction( display );
    assertNotNull( "SelectionEvent was not fired", events[ 0 ] );
    assertEquals( table, events[ 0 ].getSource() );
    assertEquals( item2, events[ 0 ].item );
    assertEquals( true, events[ 0 ].doit );
    assertEquals( 0, events[ 0 ].x );
    assertEquals( 0, events[ 0 ].y );
    assertEquals( 0, events[ 0 ].width );
    assertEquals( 0, events[ 0 ].height );
    assertEquals( 0, table.getSelectionCount() );
    assertEquals( -1, table.getSelectionIndex() );
  }

  public void testRedraw() {
    final Table[] table = { null };
    shell.setSize( 100, 100 );
    Button button = new Button( shell, SWT.PUSH );
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        table[ 0 ] = new Table( shell, SWT.VIRTUAL );
        table[ 0 ].setItemCount( 500 );
        table[ 0 ].setSize( 90, 90 );
        assertFalse( isItemVirtual( table[ 0 ], 0 ) );
        table[ 0 ].clearAll();
        table[ 0 ].redraw();
      }
    } );
    shell.open();
    Fixture.fakeNewRequest( display );
    String buttonId = WidgetUtil.getId( button );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId  );
    Fixture.executeLifeCycleFromServerThread();
    assertFalse( isItemVirtual( table[ 0 ], 0  ) );
  }

  public void testNoUnwantedResolveItems() {
    shell.setSize( 100, 100 );
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setSize( 90, 90 );
    table.setItemCount( 1000 );
    shell.open();
    String tableId = WidgetUtil.getId( table );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_SET_DATA_INDEX, "500,501,502,503" );
    Fixture.fakeRequestParam( tableId + ".topIndex", "500" );
    ILifeCycle lifeCycle = RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void beforePhase( PhaseEvent event ) {
        table.redraw();
      }

      public void afterPhase( PhaseEvent event ) {
      }

      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
    } );
    Fixture.executeLifeCycleFromServerThread();

    assertTrue( isItemVirtual( table, 499 ) );
    assertTrue( isItemVirtual( table, 800 ) );
    assertTrue( isItemVirtual( table, 999 ) );
  }

  public void testClearVirtual() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    shell.setSize( 100, 100 );
    shell.setLayout( new FillLayout() );
    final Table table = new Table( shell, SWT.VIRTUAL );
    table.setItemCount( 100 );
    shell.layout();
    shell.open();
    ITableAdapter adapter = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    // precondition: all items are resolved (TableItem#cached == true)
    // resolve all items and ensure
    for( int i = 0; i < table.getItemCount(); i++ ) {
      table.getItem( i ).getText();
    }
    assertFalse( adapter.isItemVirtual( table.getItemCount() - 1 ) );
    //
    final int lastItemIndex = table.getItemCount() - 1;
    String lastItemId = WidgetUtil.getId( table.getItem( lastItemIndex ) );
    // fake one request that would initialize the UI
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread();
    // run actual request
    Fixture.fakeNewRequest( display );
    ILifeCycle lifeCycle = RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( PhaseEvent event ) {
        table.clear( lastItemIndex );
      }
      public void afterPhase( PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
    } );
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected
      = "var w = wm.findWidgetById( \""
      + lastItemId
      + "\" );w.clear()";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testSetDataEvent() {
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    Listener listener = new Listener() {
      public void handleEvent( Event event ) {
        Item item = ( Item )event.item;
        item.setText( "Item " + event.index );
      }
    };
    table.addListener( SWT.SetData, listener );
    table.setSize( 90, 90 );
    table.setItemCount( 1000 );
    shell.layout();
    shell.open();
    String tableId = WidgetUtil.getId( table );
    // Run test request
    assertTrue( isItemVirtual( table, 500 ) ); // ensure precondition
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".topItemIndex", "500" );
    Fixture.executeLifeCycleFromServerThread();
    // Remove SetData listener to not accidentially resolve item with asserts
    table.removeListener( SWT.SetData, listener );
    // assert request results
    assertFalse( isItemVirtual( table, 500 ) );
    assertFalse( isItemVirtual( table, 502 ) );
    assertTrue( isItemVirtual( table, 510 ) );
    assertEquals( "Item 500", table.getItem( 500 ).getText() );
    assertEquals( "Item 502", table.getItem( 502 ).getText() );
  }

  public void testReadSelection() {
    Table table = new Table( shell, SWT.MULTI );
    String tableId = WidgetUtil.getId( table );
    TableItem item1 = new TableItem( table, SWT.NONE );
    String item1Id = WidgetUtil.getId( item1 );
    TableItem item2 = new TableItem( table, SWT.NONE );
    String item2Id = WidgetUtil.getId( item2 );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".selection", item1Id + "," + item2Id );
    Fixture.executeLifeCycleFromServerThread();

    TableItem[] selectedItems = table.getSelection();
    assertEquals( 2, selectedItems.length );
    assertSame( item1, selectedItems[ 1 ] );
    assertSame( item2, selectedItems[ 0 ] );
  }

  public void testReadSelection_UnresolvedItem() {
    Table table = new Table( shell, SWT.MULTI | SWT.VIRTUAL );
    String tableId = WidgetUtil.getId( table );
    table.setItemCount( 3 );
    TableItem item = table.getItem( 0 );
    item.setText( "Item 1" );
    String itemId = WidgetUtil.getId( item );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".selection", itemId + "," + tableId + "#2" );
    Fixture.executeLifeCycleFromServerThread();

    int[] selectedIndices = table.getSelectionIndices();
    assertEquals( 2, selectedIndices.length );
    assertEquals( 0, selectedIndices[ 1 ] );
    assertEquals( 2, selectedIndices[ 0 ] );
    assertTrue( isItemVirtual( table, 2 ) );
  }

  /*
   * Ensures that checkData calls with an invalid index are silently ignored.
   * This may happen, when the itemCount is reduced during a SetData event.
   * Queued SetData events may then have stale (out-of-bounds) indices.
   * See 235368: [table] [table] ArrayIndexOutOfBoundsException in virtual
   *     TableViewer
   *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=235368
   */
  public void testReduceItemCountInSetData() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    shell.setSize( 100, 100 );
    Table table = new Table( shell, SWT.VIRTUAL );
    table.addListener( SWT.SetData, new Listener() {
      public void handleEvent( Event event ) {
        fail( "Must not trigger SetData event" );
      }
    } );

    Fixture.fakePhase( PhaseId.READ_DATA );
    table.setItemCount( 1 );
    ITableAdapter adapter = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    adapter.checkData( 0 );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    table.setItemCount( 0 );
    int eventCount = 0;
    while( ProcessActionRunner.executeNext() ) {
      eventCount++;
    }
    while( SetDataEvent.executeNext() ) {
      eventCount++;
    }
    assertEquals( 1, eventCount );
  }

  public void testGetItemMetrics() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Table table = new Table( shell, SWT.NONE );
    table.setHeaderVisible( true );
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 200 );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( "item1" );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setText( "item2" );
    TableItem item3 = new TableItem( table, SWT.NONE );
    item3.setText( "item3" );

    item2.setImage( image );
    ItemMetrics[] metrics = TableLCAUtil.getItemMetrics( table );
    assertTrue( metrics[ 0 ].imageWidth > 0 );

    item1.setImage( image );
    metrics = TableLCAUtil.getItemMetrics( table );
    int defaultLeftPadding = 3;
    assertEquals( defaultLeftPadding, metrics[ 0 ].imageLeft );
    assertTrue( metrics[ 0 ].imageWidth > 0 );

    item1.setImage( image );
    Fixture.preserveWidgets();
    item1.setImage( ( Image )null );
    assertTrue( TableLCAUtil.hasItemMetricsChanged( table ) );

    // spacing must be respected
    int defaultSpacing = 3;
    int expected =   metrics[ 0 ].imageLeft
                   + metrics[ 0 ].imageWidth
                   + defaultSpacing;
    assertEquals( expected, metrics[ 0 ].textLeft );

    // left offset must be compensated
    ITableAdapter adapter
      = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 10 );
    metrics = TableLCAUtil.getItemMetrics( table );
    assertEquals( 0, metrics[ 0 ].left );
    assertEquals( defaultLeftPadding, metrics[ 0 ].imageLeft );
    expected =   metrics[ 0 ].imageLeft
               + metrics[ 0 ].imageWidth
               + defaultSpacing;
    assertEquals( expected, metrics[ 0 ].textLeft );

    // image must not exceed right column border
    column.setWidth( 12 );
    metrics = TableLCAUtil.getItemMetrics( table );
    assertEquals( 9, metrics[ 0 ].imageWidth );

    Fixture.preserveWidgets();
    item1.setImage( image );
    table.setSelection( item1 );
    assertTrue( TableLCAUtil.hasItemMetricsChanged( table ) );
  }

  public void testGetItemMetricsWithCheckBox() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 200, 200 );
    shell.setLayout( new FillLayout() );
    Table table = new Table( shell, SWT.CHECK );
    table.setHeaderVisible( true );
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 30 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 400 );
    ITableAdapter adapter = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 5 );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( "item1" );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setText( "item2" );
    TableItem item3 = new TableItem( table, SWT.NONE );
    item3.setText( "item3" );
    item2.setImage( image );
    ItemMetrics[] metrics = TableLCAUtil.getItemMetrics( table );
    assertEquals( 24, metrics[ 0 ].imageLeft );
    assertEquals( 6, metrics[ 0 ].imageWidth );
  }

  public void testGetItemMetricsImageCutOffInSecondColumn() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 200, 200 );
    shell.setLayout( new FillLayout() );
    Table table = new Table( shell, SWT.NONE );
    table.setHeaderVisible( true );
    TableColumn column = new TableColumn( table, SWT.NONE );
    column.setText( "column1" );
    column.setWidth( 400 );
    TableColumn column2 = new TableColumn( table, SWT.NONE );
    column2.setText( "column2" );
    column2.setWidth( 30 );
    ITableAdapter adapter = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 5 );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( "item1" );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setText( "item2" );
    TableItem item3 = new TableItem( table, SWT.NONE );
    item3.setText( "item3" );
    item2.setImage( 1, image );
    ItemMetrics[] metrics = TableLCAUtil.getItemMetrics( table );
    assertEquals( 403, metrics[ 1 ].imageLeft );
    assertEquals( 27, metrics[ 1 ].imageWidth );
  }

  public void testGetItemMetricsWithoutColumns() {
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setBounds( 0, 0, 800, 600 );
    shell.setLayout( new FillLayout() );
    Table table = new Table( shell, SWT.NONE );
    table.setHeaderVisible( true );
    TableItem item1 = new TableItem( table, SWT.NONE );
    item1.setText( "item1" );
    TableItem item2 = new TableItem( table, SWT.NONE );
    item2.setText( "item2" );
    TableItem item3 = new TableItem( table, SWT.NONE );
    item3.setText( "item3" );

    ItemMetrics[] metrics = TableLCAUtil.getItemMetrics( table );
    assertEquals( 0, metrics[ 0 ].imageWidth );

    item2.setImage( image );
    metrics = TableLCAUtil.getItemMetrics( table );
    assertTrue( metrics[ 0 ].imageWidth > 0 );
    int defaultLeftPadding = 3;
    assertEquals( defaultLeftPadding, metrics[ 0 ].imageLeft );
    assertTrue( metrics[ 0 ].imageWidth > 0 );

    item1.setImage( image );
    Fixture.preserveWidgets();
    item1.setImage( ( Image )null );
    assertTrue( TableLCAUtil.hasItemMetricsChanged( table ) );

    // spacing must be respected
    int defaultSpacing = 3;
    int expected =   metrics[ 0 ].imageLeft
                   + metrics[ 0 ].imageWidth
                   + defaultSpacing;
    assertEquals( expected, metrics[ 0 ].textLeft );

    // left offset must be compensated
    ITableAdapter adapter = ( ITableAdapter )table.getAdapter( ITableAdapter.class );
    adapter.setLeftOffset( 10 );
    metrics = TableLCAUtil.getItemMetrics( table );
    assertEquals( 0, metrics[ 0 ].left );
    assertEquals( defaultLeftPadding, metrics[ 0 ].imageLeft );
    expected =   metrics[ 0 ].imageLeft
               + metrics[ 0 ].imageWidth
               + defaultSpacing;
    assertEquals( expected, metrics[ 0 ].textLeft );

    Fixture.preserveWidgets();
    item1.setImage( image );
    table.setSelection( item1 );
    assertTrue( TableLCAUtil.hasItemMetricsChanged( table ) );
  }

  public void testHasAlwaysHideSelection_InitiallyFalse() {
    Table table = new Table( shell, SWT.NONE );

    assertEquals( Boolean.FALSE, TableLCA.hasAlwaysHideSelection( table ) );
  }

  public void testHasAlwaysHideSelection_TrueAfterSetData() {
    Table table = new Table( shell, SWT.NONE );
    table.setData( Table.ALWAYS_HIDE_SELECTION, Boolean.TRUE );

    assertEquals( Boolean.TRUE, TableLCA.hasAlwaysHideSelection( table ) );
  }

  public void testRenderAlwaysHideSelection() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    Fixture.fakeNewRequest();
    table.setData( Table.ALWAYS_HIDE_SELECTION, Boolean.TRUE );
    TableLCA lca = new TableLCA();

    Fixture.fakeNewRequest();
    lca.renderChanges( table );

    String markup = Fixture.getAllMarkup();
    String expected = "w.setAlwaysHideSelection( true )";
    assertTrue( markup.contains( expected ) );
  }

  public void testWriteScrollbarsVisible() throws IOException {
    Fixture.fakeNewRequest();
    Table table = new Table( shell, SWT.NO_SCROLL );
    TableLCA lca = new TableLCA();
    lca.renderChanges( table );
    String markup = Fixture.getAllMarkup();
    String expected = "w.setScrollBarsVisible( false, false );";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testWriteScrollbarsSelectionListener() throws IOException {
    Fixture.fakeNewRequest();
    Table table = new Table( shell, SWT.NONE );
    SelectionAdapter listener = new SelectionAdapter() {
    };
    table.getHorizontalBar().addSelectionListener( listener );
    TableLCA lca = new TableLCA();
    lca.renderChanges( table );
    String markup = Fixture.getAllMarkup();
    String expected = "w.setHasScrollBarsSelectionListener( true );";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testWriteFocusIndex() throws IOException {
    Table table = new Table( shell, SWT.NO_SCROLL );
    for( int i = 0; i < 3; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    TableLCA lca = new TableLCA();
    Fixture.fakeNewRequest();
    lca.renderChanges( table );
    String markup = Fixture.getAllMarkup();
    String expected = "w.setFocusItem";
    assertTrue( markup.indexOf( expected ) == -1 );
    table.setSelection( 0 );
    Fixture.fakeNewRequest();
    Fixture.markInitialized( table );
    lca.preserveValues( table );
    table.getItem( 0 ).dispose();
    lca.renderChanges( table );
    markup = Fixture.getAllMarkup();
    expected = "w.setFocusItem( null )";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testReadFocusItem() {
    Table table = new Table( shell, SWT.MULTI );
    for( int i = 0; i < 5; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    String tableId = WidgetUtil.getId( table );
    // ensure that reading selection parameter does not override focusIndex
    Fixture.fakeRequestParam( tableId + ".focusItem", indexToId( table, 4 ) );
    String items = indicesToIds( table, new int[]{ 0, 1, 2, 3, 4 } );
    Fixture.fakeRequestParam( tableId + ".selection", items );
    TableLCA tableLCA = new TableLCA();
    tableLCA.readData( table );
    assertEquals( 4, tableAdapter.getFocusIndex() );
  }

  public void testReadUnresolvedFocusItem() {
    Table table = new Table( shell, SWT.MULTI );
    for( int i = 0; i < 5; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    String tableId = WidgetUtil.getId( table );
    // ensure that reading selection parameter does not override focusIndex
    Fixture.fakeRequestParam( tableId + ".focusItem", tableId + "#4" );
    String items = indicesToIds( table, new int[]{ 0, 1, 2, 3, 4 } );
    Fixture.fakeRequestParam( tableId + ".selection", items );
    TableLCA tableLCA = new TableLCA();
    tableLCA.readData( table );
    assertEquals( 4, tableAdapter.getFocusIndex() );
  }

  public void testReadTopIndex() {
    Table table = new Table( shell, SWT.MULTI );
    table.setSize( 485, 485 );
    for( int i = 0; i < 115; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    String tableId = WidgetUtil.getId( table );
    int[] indices = new int[]{
      114,70,71,72,73,74,75,76,77,78,79,80,81,82,83,
      84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,
      99,100,101,102,103,104,105,106,107,108,109,
      110,111,112,113,0
    };
    String items = indicesToIds( table, indices );
    Fixture.fakeRequestParam( tableId + ".topIndex", "0" );
    Fixture.fakeRequestParam( tableId + ".selection", items );
    TableLCA tableLCA = new TableLCA();
    tableLCA.readData( table );
    assertEquals( 0, table.getTopIndex() );
  }

  public void testWriteEnableCellToolTip() throws IOException {
    Table table = new Table( shell, SWT.NONE );
    createTableItems( table, 5 );
    Fixture.fakeNewRequest();
    table.setData( ICellToolTipProvider.ENABLE_CELL_TOOLTIP, Boolean.TRUE );
    TableLCA tableLCA = new TableLCA();
    tableLCA.renderChanges( table );
    String markup = Fixture.getAllMarkup();
    String expected = "w.setEnableCellToolTip( true )";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testGetCellToolTipText() {
    Table table = new Table( shell, SWT.NONE );
    createTableItems( table, 5 );
    final ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( table );
    adapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "[" );
        buffer.append( WidgetUtil.getId( item ) );
        buffer.append( "," );
        buffer.append( columnIndex );
        buffer.append( "]" );
        adapter.setCellToolTipText( buffer.toString() );
      }
    } );
    Fixture.fakeNewRequest();
    Fixture.executeLifeCycleFromServerThread();
    String markup = Fixture.getAllMarkup();
    String expected = "w.setCellToolTipText(";
    assertTrue( markup.indexOf( expected ) == -1 );
    String itemId = WidgetUtil.getId( table.getItem( 2 ) );
    processCellToolTipRequest( table, itemId, 0 );
    markup = Fixture.getAllMarkup();
    expected = "w.setCellToolTipText( \"[" + itemId + ",0]\" );";
    assertTrue( markup.indexOf( expected ) != -1 );
  }

  public void testCellTooltipRequestForMissingCells() {
    Table table = new Table( shell, SWT.NONE );
    createTableItems( table, 3 );
    final StringBuffer log = new StringBuffer();
    ICellToolTipAdapter tableAdapter
      = ( ICellToolTipAdapter )table.getAdapter( ICellToolTipAdapter.class );
    tableAdapter.setCellToolTipProvider( new ICellToolTipProvider() {
      public void getToolTipText( Item item, int columnIndex ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "[" );
        buffer.append( WidgetUtil.getId( item ) );
        buffer.append( "," );
        buffer.append( columnIndex );
        buffer.append( "]" );
        log.append( buffer.toString() );
      }
    } );
    String itemId = WidgetUtil.getId( table.getItem( 0 ) );
    processCellToolTipRequest( table, itemId, 0 );
    String expected = "[" + itemId + ",0]";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    itemId = WidgetUtil.getId( table.getItem( 2 ) );
    processCellToolTipRequest( table, itemId, 0 );
    expected = "[" + itemId + ",0]";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    processCellToolTipRequest( table, "xyz", 0 );
    assertEquals( "", log.toString() );
    processCellToolTipRequest( table, itemId, 1 );
    assertEquals( "", log.toString() );
    createTableColumns( table, 2 );
    processCellToolTipRequest( table, itemId, 1 );
    expected = "[" + itemId + ",1]";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    processCellToolTipRequest( table, itemId, 2 );
    assertEquals( "", log.toString() );
  }

  public void testScrollbarsSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List<String> log = new ArrayList<String>();
    Table table = new Table( shell, SWT.NONE );
    for( int i = 0; i < 20; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( "scrollbarSelected" );
      }
    };
    table.getHorizontalBar().addSelectionListener( listener );
    Fixture.fakeNewRequest();
    String tableId = WidgetUtil.getId( table );
    Fixture.fakeRequestParam( tableId + ".scrollLeft", "10" );
    Fixture.readDataAndProcessAction( table );
    assertEquals( 1, log.size() );
    assertEquals( 10, table.getHorizontalBar().getSelection() );
    log.clear();
    table.getVerticalBar().addSelectionListener( listener );
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( tableId + ".scrollLeft", "10" );
    Fixture.fakeRequestParam( tableId + ".topItemIndex", "10" );
    Fixture.readDataAndProcessAction( table );
    assertEquals( 2, log.size() );
    assertEquals( 10 * table.getItemHeight(), table.getVerticalBar().getSelection());
  }

  public void testSelectionEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List<Widget> log = new ArrayList<Widget>();
    Table table = new Table( shell, SWT.NONE );
    String tableId = WidgetUtil.getId( table );
    for( int i = 0; i < 5; i++ ) {
      new TableItem( table, SWT.NONE );
    }
    TableItem item = table.getItem( 3 );
    String itemId = WidgetUtil.getId( item );
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( event.item );
      }
    };
    table.addSelectionListener( listener );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".selection", itemId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, itemId );
    Fixture.readDataAndProcessAction( table );

    assertEquals( 1, log.size() );
    assertSame( item, log.get( 0 ) );
  }

  public void testSelectionEvent_UnresolvedItem() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final java.util.List<Widget> log = new ArrayList<Widget>();
    Table table = new Table( shell, SWT.VIRTUAL );
    String tableId = WidgetUtil.getId( table );
    table.setItemCount( 3 );
    SelectionListener listener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        log.add( event.item );
      }
    };
    table.addSelectionListener( listener );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( tableId + ".selection", tableId + "#2" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, tableId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED_ITEM, tableId + "#2" );
    Fixture.readDataAndProcessAction( table );

    assertEquals( 1, log.size() );
    assertSame( table.getItem( 2 ), log.get( 0 ) );
  }

  // Ensures that writeItemCount is called first
  // see bug 326941
  public void testWriteItemCount() throws Exception {
    Table table = new Table( shell, SWT.NONE );
    createTableItems( table, 5 );
    table.setBounds( 10, 10, 10, 10 );
    Fixture.fakeNewRequest();
    TableLCA tableLCA = new TableLCA();
    tableLCA.renderChanges( table );
    String markup = Fixture.getAllMarkup();
    int index1 = markup.indexOf( "w.setItemCount( 5 )" );
    int index2 = markup.indexOf( "w.setSpace( 10, 10, 10, 10 )" );
    assertTrue( index1 < index2 );
  }

  public void testRenderNonNegativeImageWidth() {
    Table table = new Table( shell, SWT.NONE );
    TableColumn column = new TableColumn( table, SWT.NONE );
    TableItem item = new TableItem( table, SWT.NONE );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    item.setImage( image );
    column.setWidth( 2 );
    ItemMetrics[] metrics = TableLCAUtil.getItemMetrics( table );
    assertEquals( 1, metrics.length );
    assertEquals( 0, metrics[ 0 ].imageWidth );
  }


  public void testWriteFixedColumns() throws IOException {
    Table table = new Table( shell, SWT.NO_SCROLL );
    table.setData( "fixedColumns", new Integer( 0 ) );
    TableLCA lca = new TableLCA();
    Fixture.fakeNewRequest();
    lca.renderChanges( table );
    String markup = Fixture.getAllMarkup();
    String widgetRef = "wm.findWidgetById( \"" + WidgetUtil.getId( table ) + "\" )";
    String expected = "TreeUtil.setFixedColumns( " + widgetRef + ", 0 )";
    assertTrue( markup.indexOf( expected ) == -1 );
  }


  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  private static void createTableColumns( Table table, int count ) {
    for( int i = 0; i < count; i++ ) {
      new TableColumn( table, SWT.NONE );
    }
  }

  private static void createTableItems( Table table, int count ) {
    for( int i = 0; i < count; i++ ) {
      new TableItem( table, SWT.NONE );
    }
  }

  private static void processCellToolTipRequest( Table table, String itemId, int column ) {
    Fixture.fakeNewRequest( table.getDisplay() );
    String tableId = WidgetUtil.getId( table );
    Fixture.fakeRequestParam( JSConst.EVENT_CELL_TOOLTIP_REQUESTED, tableId );
    String cellString = itemId + "," + column;
    Fixture.fakeRequestParam( JSConst.EVENT_CELL_TOOLTIP_DETAILS, cellString );
    Fixture.executeLifeCycleFromServerThread();
  }

  private static boolean isItemVirtual( Table table, int index ) {
    Object adapter = table.getAdapter( ITableAdapter.class );
    ITableAdapter tableAdapter = ( ITableAdapter )adapter;
    return tableAdapter.isItemVirtual( index );
  }

  private static String indicesToIds( Table table, int[] indices ) {
    String items = new String();
    for( int i = 0; i < indices.length; i++ ) {
      items += indexToId( table, indices[ i ] );
      if( i != indices.length - 1 ) {
        items += ",";
      }
    }
    return items;
  }

  private static String indexToId( Table table, int index ) {
    return WidgetUtil.getId( table.getItem( index ) );
  }

}
