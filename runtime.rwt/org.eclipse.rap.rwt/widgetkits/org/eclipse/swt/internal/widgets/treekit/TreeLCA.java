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
package org.eclipse.swt.internal.widgets.treekit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.theme.JsonArray;
import org.eclipse.rwt.internal.theme.JsonObject;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;

public final class TreeLCA extends AbstractWidgetLCA {

  // Property names used by preserve mechanism
  private static final String PROP_SELECTION_LISTENERS = "selectionListeners";
  static final String PROP_HEADER_HEIGHT = "headerHeight";
  static final String PROP_HEADER_VISIBLE = "headerVisible";
  static final String PROP_COLUMN_COUNT = "columnCount";
  static final String PROP_TREE_COLUMN = "treeColumn";
  static final String PROP_ITEM_HEIGHT = "itemHeight";
  static final String PROP_ITEM_COUNT = "itemCount";
  static final String PROP_TOP_ITEM_INDEX = "topItemIndex";
  static final String PROP_SCROLL_LEFT = "scrollLeft";
  static final String PROP_HAS_H_SCROLL_BAR = "hasHScrollBar";
  static final String PROP_HAS_V_SCROLL_BAR = "hasVScrollBar";
  static final String PROP_ITEM_METRICS = "itemMetrics";
  static final String PROP_LINES_VISIBLE = "linesVisible";
  static final String PROP_SCROLLBARS_SELECTION_LISTENER = "scrollBarsSelectionListeners";
  static final String PROP_ENABLE_CELL_TOOLTIP = "enableCellToolTip";

  private static final Integer ZERO = new Integer( 0 );

  public void preserveValues( Widget widget ) {
    Tree tree = ( Tree )widget;
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    adapter.preserve( PROP_SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( tree ) ) );
    adapter.preserve( PROP_HEADER_HEIGHT, new Integer( tree.getHeaderHeight() ) );
    adapter.preserve( PROP_HEADER_VISIBLE, Boolean.valueOf( tree.getHeaderVisible() ) );
    adapter.preserve( PROP_LINES_VISIBLE, Boolean.valueOf( tree.getLinesVisible() ) );
    preserveItemMetrics( tree );
    adapter.preserve( PROP_COLUMN_COUNT, new Integer( tree.getColumnCount() ) );
    adapter.preserve( PROP_TREE_COLUMN, getTreeColumn( tree ) );
    adapter.preserve( PROP_ITEM_HEIGHT, new Integer( tree.getItemHeight() ) );
    adapter.preserve( PROP_ITEM_COUNT, new Integer( tree.getItemCount() ) );
    adapter.preserve( PROP_SCROLL_LEFT, getScrollLeft( tree ) );
    adapter.preserve( PROP_TOP_ITEM_INDEX, new Integer( getTopItemIndex( tree ) ) );
    adapter.preserve( PROP_HAS_H_SCROLL_BAR, hasHScrollBar( tree ) );
    adapter.preserve( PROP_HAS_V_SCROLL_BAR, hasVScrollBar( tree ) );
    adapter.preserve( PROP_SCROLLBARS_SELECTION_LISTENER, hasScrollBarsSelectionListener( tree ) );
    adapter.preserve( PROP_ENABLE_CELL_TOOLTIP,
                      new Boolean( CellToolTipUtil.isEnabledFor( tree ) ) );
    WidgetLCAUtil.preserveCustomVariant( tree );
  }

  public void readData( Widget widget ) {
    Tree tree = ( Tree )widget;
    readSelection( tree );
    readScrollLeft( tree );
    readTopItemIndex( tree );
    processWidgetSelectedEvent( tree );
    processWidgetDefaultSelectedEvent( tree );
    readCellToolTipTextRequested( tree );
    ControlLCAUtil.processMouseEvents( tree );
    ControlLCAUtil.processKeyEvents( tree );
    ControlLCAUtil.processMenuDetect( tree );
    WidgetLCAUtil.processHelp( tree );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    ITreeAdapter adapter = getTreeAdapter( tree );
    JSWriter writer = JSWriter.getWriterFor( tree );
    JsonObject argsMap = new JsonObject();
    argsMap.append( "appearance", "tree" );
    if( ( tree.getStyle() & SWT.VIRTUAL ) != 0 ) {
      argsMap.append( "virtual", true );
    }
    if( ( tree.getStyle() & SWT.NO_SCROLL ) != 0 ) {
      argsMap.append( "noScroll", true );
    }
    if( ( tree.getStyle() & SWT.MULTI ) != 0 ) {
      argsMap.append( "multiSelection", true );
    }
    if( ( tree.getStyle() & SWT.CHECK ) != 0 ) {
      int[] checkMetrics = new int[] { adapter.getCheckLeft(), adapter.getCheckWidth() };
      argsMap.append( "check", true );
      argsMap.append( "checkBoxMetrics", JsonArray.valueOf( checkMetrics ) );
    }
    if( ( tree.getStyle() & SWT.FULL_SELECTION ) != 0 ) {
      argsMap.append( "fullSelection", true );
    } else {
      Rectangle textMargin = getTreeAdapter( tree ).getTextMargin();
      int[] selectionPadding = new int[] { textMargin.x, textMargin.width - textMargin.x };
      argsMap.append( "selectionPadding", JsonArray.valueOf( selectionPadding ) );
    }
    argsMap.append( "indentionWidth", adapter.getIndentionWidth() );
    Object[] args = new Object[]{ new JSVar( argsMap.toString() ) };
    writer.newWidget( "org.eclipse.rwt.widgets.Tree", args );
    ControlLCAUtil.writeStyleFlags( tree );
  }

  public void renderChanges( Widget widget ) throws IOException {
    Tree tree = ( Tree )widget;
    writeItemCount( tree );
    ControlLCAUtil.writeChanges( tree );
    writeItemHeight( tree );
    writeItemMetrics( tree );
    // NOTE : Client currently requires itemMetrics before columnCount
    writeColumnCount( tree );
    writeLinesVisible( tree );
    writeTreeColumn( tree );
    writeTopItem( tree );
    writeScrollBars( tree );
    updateSelectionListener( tree );
    writeScrollBarsSelectionListener( tree );
    writeHeaderHeight( tree );
    writeHeaderVisible( tree );
    writeScrollLeft( tree );
    writeEnableCellToolTip( tree );
    writeCellToolTipText( tree );
    WidgetLCAUtil.writeCustomVariant( tree );
  }

  public void renderDispose( Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void doRedrawFake( Control control ) {
    int evtId = ControlEvent.CONTROL_RESIZED;
    ControlEvent evt = new ControlEvent( control, evtId );
    evt.processEvent();
  }

  private static void processWidgetSelectedEvent( Tree tree ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String eventName = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
      String itemId = request.getParameter( eventName + ".item" );
      Item treeItem = getItem( tree, itemId );
      String detailStr = request.getParameter( eventName + ".detail" );
      int detail = "check".equals( detailStr ) ? SWT.CHECK : SWT.NONE;
      int eventType = SelectionEvent.WIDGET_SELECTED;
      int stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      SelectionEvent event = new SelectionEvent( tree,
                                                 treeItem,
                                                 eventType,
                                                 bounds,
                                                 stateMask,
                                                 null,
                                                 true,
                                                 detail );
      event.processEvent();
    }
  }

  private static void processWidgetDefaultSelectedEvent( Tree tree ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String eventName = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( tree, eventName ) ) {
      String itemId = request.getParameter( eventName + ".item" );
      Item treeItem = getItem( tree, itemId );
      int eventType = SelectionEvent.WIDGET_DEFAULT_SELECTED;
      SelectionEvent event = new SelectionEvent( tree, treeItem, eventType );
      event.stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
      event.processEvent();
    }
  }

  /////////////////////////////////////////////
  // Helping methods to read client-side state

  private static void readSelection( Tree tree ) {
    String value = WidgetLCAUtil.readPropertyValue( tree, "selection" );
    if( value != null ) {
      String[] values = value.split( "," );
      TreeItem[] selectedItems = new TreeItem[ values.length ];
      boolean validItemFound = false;
      for( int i = 0; i < values.length; i++ ) {
        selectedItems[ i ] = getItem( tree, values[ i ] );
        if( selectedItems[ i ] != null ) {
          validItemFound = true;
        }
      }
      if( !validItemFound ) {
        selectedItems = new TreeItem[ 0 ];
      }
      tree.setSelection( selectedItems );
    }
  }

  private static void readScrollLeft( Tree tree ) {
    String left = WidgetLCAUtil.readPropertyValue( tree, "scrollLeft" );
    if( left != null ) {
      int leftOffset = parsePosition( left );
      final ITreeAdapter treeAdapter = getTreeAdapter( tree );
      treeAdapter.setScrollLeft( leftOffset );
      processScrollBarSelection( tree.getHorizontalBar(), leftOffset );
    }
  }

  private static void readTopItemIndex( final Tree tree ) {
    String topItemIndex = WidgetLCAUtil.readPropertyValue( tree, "topItemIndex" );
    if( topItemIndex != null ) {
      final ITreeAdapter treeAdapter = getTreeAdapter( tree );
      int newIndex = parsePosition( topItemIndex );
      int topOffset = newIndex * tree.getItemHeight();
      treeAdapter.setTopItemIndex( newIndex );
      processScrollBarSelection( tree.getVerticalBar(), topOffset );
    }
  }

  private static int parsePosition( final String position ) {
    int result = 0;
    try {
      result = Integer.valueOf( position ).intValue();
    } catch( NumberFormatException e ) {
      // ignore and use default value
    }
    return result;
  }

  //////////////////////////////////////////////////////////////
  // Helping methods to write JavaScript for changed properties

  private static void writeItemHeight( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( tree.getItemHeight( ) );
    if( WidgetLCAUtil.hasChanged( tree, PROP_ITEM_HEIGHT, newValue ) ) {
      writer.set( PROP_ITEM_HEIGHT, "itemHeight", newValue, new Integer( 16 ) );
    }
  }

  private static void writeItemCount( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( tree.getItemCount() );
    writer.set( PROP_ITEM_COUNT, "itemCount", newValue, ZERO );
  }

  static void writeItemMetrics( final Tree tree ) throws IOException {
    ItemMetrics[] itemMetrics = getItemMetrics( tree );
    if( hasItemMetricsChanged( tree, itemMetrics ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      for( int i = 0; i < itemMetrics.length; i++ ) {
        Object[] args = new Object[] {
          new Integer( i ),
          new Integer( itemMetrics[ i ].left ),
          new Integer( itemMetrics[ i ].width ),
          new Integer( itemMetrics[ i ].imageLeft ),
          new Integer( itemMetrics[ i ].imageWidth ),
          new Integer( itemMetrics[ i ].textLeft ),
          new Integer( itemMetrics[ i ].textWidth )
        };
        writer.set( "itemMetrics", args );
      }
    }
  }

  private static void writeHeaderHeight( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( tree.getHeaderHeight() );
    writer.set( PROP_HEADER_HEIGHT, "headerHeight", newValue, null );
  }

  private static void writeColumnCount( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( tree.getColumnCount() );
    if( WidgetLCAUtil.hasChanged( tree, PROP_COLUMN_COUNT, newValue ) ) {
      writer.set( PROP_COLUMN_COUNT, "columnCount", newValue, ZERO );
    }
  }

  private static void writeTreeColumn( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = getTreeColumn( tree );
    if( WidgetLCAUtil.hasChanged( tree, PROP_TREE_COLUMN, newValue ) ) {
      writer.set( PROP_TREE_COLUMN, "treeColumn", newValue, ZERO );
    }
  }

  private static void writeTopItem( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = new Integer( getTopItemIndex( tree ) );
    if( WidgetLCAUtil.hasChanged( tree, PROP_TOP_ITEM_INDEX, newValue ) ) {
      writer.set( PROP_TOP_ITEM_INDEX, "topItemIndex", newValue, ZERO );
    }
  }

  private static void writeHeaderVisible( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Boolean newValue = Boolean.valueOf( tree.getHeaderVisible() );
    writer.set( PROP_HEADER_VISIBLE, "headerVisible", newValue, Boolean.FALSE );
  }

  private static void writeScrollLeft( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Integer newValue = getScrollLeft( tree );
    writer.set( PROP_SCROLL_LEFT, "scrollLeft", newValue, ZERO );
  }

  private static void writeScrollBars( Tree tree ) throws IOException {
    boolean hasHChanged = WidgetLCAUtil.hasChanged( tree,
                                                    PROP_HAS_H_SCROLL_BAR,
                                                    hasHScrollBar( tree ),
                                                    Boolean.FALSE );
    boolean hasVChanged = WidgetLCAUtil.hasChanged( tree,
                                                    PROP_HAS_V_SCROLL_BAR,
                                                    hasVScrollBar( tree ),
                                                    Boolean.FALSE );
    if( hasHChanged || hasVChanged ) {
      boolean scrollX = hasHScrollBar( tree ).booleanValue();
      boolean scrollY = hasVScrollBar( tree ).booleanValue();
      JSWriter writer = JSWriter.getWriterFor( tree );
       writer.set( "scrollBarsVisible", new boolean[]{ scrollX, scrollY } );
    }
  }

  private static void writeLinesVisible( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    Boolean newValue = Boolean.valueOf( tree.getLinesVisible() );
    writer.set( PROP_LINES_VISIBLE, "linesVisible", newValue, Boolean.FALSE );
  }

  private static void updateSelectionListener( Tree tree ) throws IOException {
    Boolean newValue = Boolean.valueOf( SelectionEvent.hasListener( tree ) );
    String prop = PROP_SELECTION_LISTENERS;
    if( WidgetLCAUtil.hasChanged( tree, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      writer.set( "hasSelectionListeners", newValue );
    }
  }

  private static void writeScrollBarsSelectionListener( Tree tree ) throws IOException {
    Boolean newValue = hasScrollBarsSelectionListener( tree );
    String prop = PROP_SCROLLBARS_SELECTION_LISTENER;
    if( WidgetLCAUtil.hasChanged( tree, prop, newValue, Boolean.FALSE ) ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      writer.set( "hasScrollBarsSelectionListener", newValue );
    }
  }

  ////////////////
  // Cell tooltips

  private static void writeEnableCellToolTip( Tree tree ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( tree );
    String prop = PROP_ENABLE_CELL_TOOLTIP;
    Boolean newValue = new Boolean( CellToolTipUtil.isEnabledFor( tree ) );
    writer.set( prop, "enableCellToolTip", newValue, Boolean.FALSE );
  }

  private static void readCellToolTipTextRequested( Tree tree ) {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    adapter.setCellToolTipText( null );
    String event = JSConst.EVENT_CELL_TOOLTIP_REQUESTED;
    if( WidgetLCAUtil.wasEventSent( tree, event ) ) {
      ICellToolTipProvider provider = adapter.getCellToolTipProvider();
      if( provider != null ) {
        HttpServletRequest request = ContextProvider.getRequest();
        String cell = request.getParameter( JSConst.EVENT_CELL_TOOLTIP_DETAILS );
        String[] details = cell.split( "," );
        String itemId = details[ 0 ];
        int columnIndex = NumberFormatUtil.parseInt( details[ 1 ] );
        TreeItem item = getItem( tree, itemId );
        if( item != null && ( columnIndex == 0 || columnIndex < tree.getColumnCount() ) ) {
          provider.getToolTipText( item, columnIndex );
        }
      }
    }
  }

  private static void writeCellToolTipText( Tree tree ) throws IOException {
    ICellToolTipAdapter adapter = CellToolTipUtil.getAdapter( tree );
    String text = adapter.getCellToolTipText();
    if( text != null ) {
      JSWriter writer = JSWriter.getWriterFor( tree );
      text = WidgetLCAUtil.escapeText( text, false );
      text = WidgetLCAUtil.replaceNewLines( text, "<br/>" );
      writer.call( "setCellToolTipText", new String[]{ text } );
    }
  }

  //////////////////
  // Helping methods

  private static Integer getScrollLeft( Tree tree ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    return new Integer( treeAdapter.getScrollLeft() );
  }

  private static int getTopItemIndex( Tree tree ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    return treeAdapter.getTopItemIndex();
  }

  private static Boolean hasHScrollBar( Tree tree ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    return Boolean.valueOf( treeAdapter.hasHScrollBar() );
  }

  private static Boolean hasVScrollBar( Tree tree ) {
    ITreeAdapter treeAdapter = getTreeAdapter( tree );
    return Boolean.valueOf( treeAdapter.hasVScrollBar() );
  }

  private static Integer getTreeColumn( Tree tree ) {
    int[] values = tree.getColumnOrder();
    return new Integer( values.length > 0 ? values[ 0 ] : 0 );
  }

  private static ITreeAdapter getTreeAdapter( Tree tree ) {
    Object adapter = tree.getAdapter( ITreeAdapter.class );
    return ( ITreeAdapter )adapter;
  }

  private static Boolean hasScrollBarsSelectionListener( Tree tree ) {
    boolean result = false;
    ScrollBar horizontalBar = tree.getHorizontalBar();
    if( horizontalBar != null ) {
      result = result || SelectionEvent.hasListener( horizontalBar );
    }
    ScrollBar verticalBar = tree.getVerticalBar();
    if( verticalBar != null ) {
      result = result || SelectionEvent.hasListener( verticalBar );
    }
    return Boolean.valueOf( result );
  }

  private static void processScrollBarSelection( ScrollBar scrollBar, int selection ) {
    if( scrollBar != null ) {
      scrollBar.setSelection( selection );
      if( SelectionEvent.hasListener( scrollBar ) ) {
        int eventId = SelectionEvent.WIDGET_SELECTED;
        SelectionEvent evt = new SelectionEvent( scrollBar, null, eventId );
        evt.stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
        evt.processEvent();
      }
    }
  }

  private static TreeItem getItem( Tree tree, String itemId ) {
    TreeItem item = null;
    String[] idParts = itemId.split( "#" );
    if( idParts.length == 2 ) {
      Widget parent = WidgetUtil.find( tree, idParts[ 0 ] );
      if( parent != null ) {
        int itemIndex = Integer.parseInt( idParts[ 1 ] );
        if( WidgetUtil.getId( tree ).equals( idParts[ 0 ] ) ) {
          item = tree.getItem( itemIndex );
        } else {
          item = ( ( TreeItem )parent ).getItem( itemIndex );
        }
      }
    } else {
      item = ( TreeItem )WidgetUtil.find( tree, itemId );
    }
    return item;
  }

  /////////////////
  // Item Metrics:


  // TODO: merge with Table:
  static final class ItemMetrics {
    int left;
    int width;
    int imageLeft;
    int imageWidth;
    int textLeft;
    int textWidth;

    public boolean equals( Object obj ) {
      boolean result;
      if( obj == this ) {
        result = true;
      } else  if( obj instanceof ItemMetrics ) {
        ItemMetrics other = ( ItemMetrics )obj;
        result =  other.left == left
               && other.width == width
               && other.imageLeft == imageLeft
               && other.imageWidth == imageWidth
               && other.textLeft == textLeft
               && other.textWidth == textWidth;
      } else {
        result = false;
      }
      return result;
    }

    public int hashCode() {
      String msg = "ItemMetrics#hashCode() not implemented";
      throw new UnsupportedOperationException( msg );
    }
  }

  static ItemMetrics[] getItemMetrics( Tree tree ) {
    int columnCount = Math.max( 1, tree.getColumnCount() );
    ItemMetrics[] result = new ItemMetrics[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = new ItemMetrics();
    }
    ITreeAdapter adapter = getTreeAdapter( tree );
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ].left = adapter.getCellLeft( i );
      result[ i ].width = adapter.getCellWidth( i );
      result[ i ].imageLeft = result[ i ].left + adapter.getImageOffset( i );
      result[ i ].imageWidth = adapter.getItemImageSize( i ).x;
      result[ i ].textLeft = result[ i ].left + adapter.getTextOffset( i );
      result[ i ].textWidth = adapter.getTextMaxWidth( i );
    }
    return result;
  }

  private static void preserveItemMetrics( Tree tree ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( tree );
    adapter.preserve( PROP_ITEM_METRICS, getItemMetrics( tree ) );
  }

  private static boolean hasItemMetricsChanged( Tree tree, ItemMetrics[] metrics  ) {
    return WidgetLCAUtil.hasChanged( tree, PROP_ITEM_METRICS, metrics );
  }
}
