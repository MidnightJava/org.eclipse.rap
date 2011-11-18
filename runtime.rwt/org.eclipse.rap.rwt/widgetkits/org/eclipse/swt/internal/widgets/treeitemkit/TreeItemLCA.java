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
package org.eclipse.swt.internal.widgets.treeitemkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;


public final class TreeItemLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.TreeItem";

  static final String PROP_ITEM_COUNT = "itemCount";
  static final String PROP_TEXTS = "texts";
  static final String PROP_IMAGES = "images";
  static final String PROP_BACKGROUND = "background";
  static final String PROP_FOREGROUND = "foreground";
  static final String PROP_FONT = "font";
  static final String PROP_CELL_BACKGROUNDS = "cellBackgrounds";
  static final String PROP_CELL_FOREGROUNDS = "cellForegrounds";
  static final String PROP_CELL_FONTS = "cellFonts";
  static final String PROP_EXPANDED = "expanded";
  static final String PROP_CHECKED = "checked";
  static final String PROP_GRAYED = "grayed";
  static final String PROP_VARIANT = "variant";

  private static final int DEFAULT_ITEM_COUNT = 0;

  @Override
  public void preserveValues( Widget widget ) {
    TreeItem item = ( TreeItem )widget;
    if( isCached( item ) ) {
      preserveProperty( item, PROP_ITEM_COUNT, item.getItemCount() );
      preserveProperty( item, PROP_TEXTS, getTexts( item ) );
      preserveProperty( item, PROP_IMAGES, getImages( item ) );
      WidgetLCAUtil.preserveBackground( item, getUserBackground( item ) );
      WidgetLCAUtil.preserveForeground( item, getUserForeground( item ) );
      WidgetLCAUtil.preserveFont( item, getUserFont( item ) );
      preserveProperty( item, PROP_CELL_BACKGROUNDS, getCellBackgrounds( item ) );
      preserveProperty( item, PROP_CELL_FOREGROUNDS, getCellForegrounds( item ) );
      preserveProperty( item, PROP_CELL_FONTS, getCellFonts( item ) );
      preserveProperty( item, PROP_EXPANDED, item.getExpanded() );
      preserveProperty( item, PROP_CHECKED, item.getChecked() );
      preserveProperty( item, PROP_GRAYED, item.getGrayed() );
      preserveProperty( item, PROP_VARIANT, getVariant( item ) );
    }
  }

  public void readData( Widget widget ) {
    final TreeItem item = ( TreeItem )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "checked" );
    if( value != null ) {
      item.setChecked( Boolean.valueOf( value ).booleanValue() );
    }
    if( WidgetLCAUtil.wasEventSent( item, JSConst.EVENT_TREE_EXPANDED ) ) {
      // The event is fired before the setter is called. Order like in SWT.
      processTreeExpandedEvent( item );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          item.setExpanded( true );
        }
      } );
    }
    if( WidgetLCAUtil.wasEventSent( item, JSConst.EVENT_TREE_COLLAPSED ) ) {
      processTreeCollapsedEvent( item );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          item.setExpanded( false );
        }
      } );
    }
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    TreeItem item = ( TreeItem )widget;
    Widget parent;
    int index;
    if( item.getParentItem() == null ) {
      parent = item.getParent();
      index  = item.getParent().indexOf( item );
    } else {
      parent = item.getParentItem();
      index = item.getParentItem().indexOf( item );
    }
    IClientObject clientObject = ClientObjectFactory.getForWidget( item );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( parent ) );
    clientObject.setProperty( "index", index );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    TreeItem item = ( TreeItem )widget;
    if( isCached( item ) ) {
      renderProperty( item, PROP_ITEM_COUNT, item.getItemCount(), DEFAULT_ITEM_COUNT );
      renderProperty( item, PROP_TEXTS, getTexts( item ), getDefaultTexts( item ) );
      renderProperty( item, PROP_IMAGES, getImages( item ), new Image[ getColumnCount( item ) ] );
      WidgetLCAUtil.renderBackground( item, getUserBackground( item ) );
      WidgetLCAUtil.renderForeground( item, getUserForeground( item ) );
      WidgetLCAUtil.renderFont( item, getUserFont( item ) );
      renderProperty( item,
                      PROP_CELL_BACKGROUNDS,
                      getCellBackgrounds( item ),
                      new Color[ getColumnCount( item ) ] );
      renderProperty( item,
                      PROP_CELL_FOREGROUNDS,
                      getCellForegrounds( item ),
                      new Color[ getColumnCount( item ) ] );
      renderProperty( item,
                      PROP_CELL_FONTS,
                      getCellFonts( item ),
                      new Font[ getColumnCount( item ) ] );
      renderProperty( item, PROP_EXPANDED, item.getExpanded(), false );
      renderProperty( item, PROP_CHECKED, item.getChecked(), false );
      renderProperty( item, PROP_GRAYED, item.getGrayed(), false );
      renderProperty( item, PROP_VARIANT, getVariant( item ), null );
    }
  }

  @Override
  public void renderDispose( Widget widget ) throws IOException {
    TreeItem item = ( TreeItem )widget;
    ITreeItemAdapter itemAdapter = item.getAdapter( ITreeItemAdapter.class );
    if( !itemAdapter.isParentDisposed() ) {
      // The tree disposes the items itself on the client (faster)
      ClientObjectFactory.getForWidget( widget ).destroy();
    }
  }

  //////////////////
  // Helping methods

  private static boolean isCached( TreeItem item ) {
    Tree tree = item.getParent();
    ITreeAdapter treeAdapter = tree.getAdapter( ITreeAdapter.class );
    return treeAdapter.isCached( item );
  }

  private static String[] getTexts( TreeItem item ) {
    int columnCount = getColumnCount( item );
    String[] texts = new String[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      texts[ i ] = item.getText( i );
    }
    return texts;
  }

  private static String[] getDefaultTexts( TreeItem item ) {
    String[] result = new String[ getColumnCount( item ) ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = "";
    }
    return result;
  }

  private static Image[] getImages( TreeItem item ) {
    int columnCount = getColumnCount( item );
    Image[] images = new Image[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      images[ i ] = item.getImage( i );
    }
    return images;
  }

  private static Color getUserBackground( TreeItem item ) {
    IWidgetColorAdapter colorAdapter = item.getAdapter( IWidgetColorAdapter.class );
    return colorAdapter.getUserBackground();
  }

  private static Color getUserForeground( TreeItem item ) {
    IWidgetColorAdapter colorAdapter = item.getAdapter( IWidgetColorAdapter.class );
    return colorAdapter.getUserForeground();
  }

  private static Font getUserFont( TreeItem item ) {
    IWidgetFontAdapter fontAdapter = item.getAdapter( IWidgetFontAdapter.class );
    return fontAdapter.getUserFont();
  }

  private static Color[] getCellBackgrounds( TreeItem item ) {
    ITreeItemAdapter itemAdapter = item.getAdapter( ITreeItemAdapter.class );
    return itemAdapter.getCellBackgrounds();
  }

  private static Color[] getCellForegrounds( TreeItem item ) {
    ITreeItemAdapter itemAdapter = item.getAdapter( ITreeItemAdapter.class );
    return itemAdapter.getCellForegrounds();
  }

  private static Font[] getCellFonts( TreeItem item ) {
    ITreeItemAdapter itemAdapter = item.getAdapter( ITreeItemAdapter.class );
    return itemAdapter.getCellFonts();
  }

  private static String getVariant( TreeItem item ) {
    String result = WidgetUtil.getVariant( item );
    if( result != null ) {
      result = "variant_" + result;
    }
    return result;
  }

  private static int getColumnCount( TreeItem item ) {
    return Math.max( 1, item.getParent().getColumnCount() );
  }

  /////////////////////////////////
  // Process expand/collapse events

  private static void processTreeExpandedEvent( TreeItem item ) {
    if( TreeEvent.hasListener( item.getParent() ) ) {
      TreeEvent event = new TreeEvent( item.getParent(), item, TreeEvent.TREE_EXPANDED );
      event.processEvent();
    }
  }

  private static void processTreeCollapsedEvent( TreeItem item ) {
    if( TreeEvent.hasListener( item.getParent() ) ) {
      TreeEvent event = new TreeEvent( item.getParent(), item, TreeEvent.TREE_COLLAPSED );
      event.processEvent();
    }
  }
}
