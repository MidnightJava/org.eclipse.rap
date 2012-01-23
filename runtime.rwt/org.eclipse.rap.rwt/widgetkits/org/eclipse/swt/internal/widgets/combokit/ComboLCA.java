/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.combokit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;

import java.io.IOException;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;


public class ComboLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Combo";
  private static final String[] ALLOWED_STYLES = new String[] { "DROP_DOWN", "SIMPLE", "BORDER" };

  // Must be in sync with appearance "list-item"
  private static final int LIST_ITEM_PADDING = 3;

  // Property names for preserve-value facility
  static final String PROP_ITEMS = "items";
  static final String PROP_TEXT = "text";
  static final String PROP_SELECTION_INDEX = "selectionIndex";
  static final String PROP_SELECTION = "selection";
  static final String PROP_TEXT_LIMIT = "textLimit";
  static final String PROP_LIST_VISIBLE = "listVisible";
  static final String PROP_EDITABLE = "editable";
  static final String PROP_VISIBLE_ITEM_COUNT = "visibleItemCount";
  static final String PROP_ITEM_HEIGHT = "itemHeight";
  static final String PROP_SELECTION_LISTENER = "selection";
  static final String PROP_MODIFY_LISTENER = "modify";
  static final String PROP_VERIFY_LISTENER = "verify";

  // Default values
  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final Integer DEFAULT_SELECTION_INDEX = Integer.valueOf( -1 );
  private static final Point DEFAULT_SELECTION = new Point( 0, 0 );
  private static final int DEFAULT_VISIBLE_ITEM_COUNT = 5;

  public void preserveValues( Widget widget ) {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.preserveValues( combo );
    WidgetLCAUtil.preserveCustomVariant( combo );
    preserveProperty( combo, PROP_ITEMS, combo.getItems() );
    preserveProperty( combo, PROP_SELECTION_INDEX, Integer.valueOf( combo.getSelectionIndex() ) );
    preserveProperty( combo, PROP_SELECTION, combo.getSelection() );
    preserveProperty( combo, PROP_TEXT_LIMIT, getTextLimit( combo ) );
    preserveProperty( combo, PROP_VISIBLE_ITEM_COUNT, combo.getVisibleItemCount() );
    preserveProperty( combo, PROP_ITEM_HEIGHT, getItemHeight( combo ) );
    preserveProperty( combo, PROP_TEXT, combo.getText() );
    preserveProperty( combo, PROP_LIST_VISIBLE, combo.getListVisible() );
    preserveProperty( combo, PROP_EDITABLE, Boolean.valueOf( isEditable( combo ) ) );
    preserveListener( combo, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( combo ) );
    preserveListener( combo, PROP_MODIFY_LISTENER, ModifyEvent.hasListener( combo ) );
    preserveListener( combo, PROP_VERIFY_LISTENER, VerifyEvent.hasListener( combo ) );
  }

  public void readData( Widget widget ) {
    Combo combo = ( Combo )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "selectedItem" );
    if( value != null ) {
      combo.select( NumberFormatUtil.parseInt( value ) );
    }
    String listVisible = WidgetLCAUtil.readPropertyValue( combo, "listVisible" );
    if( listVisible != null ) {
      combo.setListVisible( Boolean.valueOf( listVisible ).booleanValue() );
    }
    readTextAndSelection( combo );
    ControlLCAUtil.processSelection( combo, null, true );
    ControlLCAUtil.processMouseEvents( combo );
    ControlLCAUtil.processKeyEvents( combo );
    ControlLCAUtil.processMenuDetect( combo );
    WidgetLCAUtil.processHelp( combo );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    IClientObject clientObject = ClientObjectFactory.getForWidget( combo );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( combo.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( combo, ALLOWED_STYLES ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    Combo combo = ( Combo )widget;
    ControlLCAUtil.renderChanges( combo );
    WidgetLCAUtil.renderCustomVariant( combo );
    renderItemHeight( combo );
    renderVisibleItemCount( combo );
    renderItems( combo );
    renderListVisible( combo );
    renderSelectionIndex( combo );
    renderEditable( combo );
    renderText( combo );
    renderSelection( combo );
    renderTextLimit( combo );
    renderListenSelection( combo );
    renderListenModify( combo );
    renderListenVerify( combo );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getForWidget( widget ).destroy();
  }

  ///////////////////////////////////////
  // Helping methods to read client state

  private static void readTextAndSelection( final Combo combo ) {
    final Point selection = readSelection( combo );
    final String value = WidgetLCAUtil.readPropertyValue( combo, "text" );
    if( value != null ) {
      if( VerifyEvent.hasListener( combo ) ) {
        // setText needs to be executed in a ProcessAcction runnable as it may
        // fire a VerifyEvent whose fields (text and doit) need to be evaluated
        // before actually setting the new value
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            combo.setText( value );
            // since text is set in process action, preserved values have to be
            // replaced
            IWidgetAdapter adapter = WidgetUtil.getAdapter( combo );
            adapter.preserve( PROP_TEXT, value );
            if( selection != null ) {
              combo.setSelection( selection );
              adapter.preserve( PROP_SELECTION, selection );
            }
         }
        } );
      } else {
        combo.setText( value );
        if( selection != null ) {
          combo.setSelection( selection );
        }
      }
    } else if( selection != null ) {
      combo.setSelection( selection );
    }
  }

  private static Point readSelection( Combo combo ) {
    Point result = null;
    String selStart = WidgetLCAUtil.readPropertyValue( combo, "selectionStart" );
    String selLength = WidgetLCAUtil.readPropertyValue( combo, "selectionLength" );
    if( selStart != null || selLength != null ) {
      result = new Point( 0, 0 );
      if( selStart != null ) {
        result.x = NumberFormatUtil.parseInt( selStart );
      }
      if( selLength != null ) {
        result.y = result.x + NumberFormatUtil.parseInt( selLength );
      }
    }
    return result;
  }

  ///////////////////////////////////////////////////
  // Helping methods to render the changed properties

  private static void renderItemHeight( Combo combo ) {
    Integer newValue = new Integer( getItemHeight( combo ) );
    if( WidgetLCAUtil.hasChanged( combo, PROP_ITEM_HEIGHT, newValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( combo );
      clientObject.set( PROP_ITEM_HEIGHT, newValue );
    }
  }

  private static void renderVisibleItemCount( Combo combo ) {
    int defValue = DEFAULT_VISIBLE_ITEM_COUNT;
    renderProperty( combo, PROP_VISIBLE_ITEM_COUNT, combo.getVisibleItemCount(), defValue );
  }

  private static void renderItems( Combo combo ) {
    renderProperty( combo, PROP_ITEMS, combo.getItems(), DEFAUT_ITEMS );
  }

  private static void renderListVisible( Combo combo ) {
    renderProperty( combo, PROP_LIST_VISIBLE, combo.getListVisible(), false );
  }

  private static void renderSelectionIndex( Combo combo ) {
    Integer newValue = new Integer( combo.getSelectionIndex() );
    boolean selectionChanged
      = WidgetLCAUtil.hasChanged( combo, PROP_SELECTION_INDEX, newValue, DEFAULT_SELECTION_INDEX );
    // The 'textChanged' statement covers the following use case:
    // combo.add( "a" );  combo.select( 0 );
    // -- in a subsequent request --
    // combo.removeAll();  combo.add( "b" );  combo.select( 0 );
    // When only examining selectionIndex, a change cannot be determined
    boolean textChanged
      = !isEditable( combo ) && WidgetLCAUtil.hasChanged( combo, PROP_TEXT, combo.getText(), "" );
    if( selectionChanged || textChanged ) {
      IClientObject clientObject = ClientObjectFactory.getForWidget( combo );
      clientObject.set( PROP_SELECTION_INDEX, newValue );
    }
  }

  private static void renderEditable( Combo combo ) {
    renderProperty( combo, PROP_EDITABLE, Boolean.valueOf( isEditable( combo ) ), Boolean.TRUE );
  }

  private static void renderText( Combo combo ) {
    if( isEditable( combo ) ) {
      renderProperty( combo, PROP_TEXT, combo.getText(), "" );
    }
  }

  private static void renderSelection( Combo combo ) {
    renderProperty( combo, PROP_SELECTION, combo.getSelection(), DEFAULT_SELECTION );
  }

  private static void renderTextLimit( Combo combo ) {
    renderProperty( combo, PROP_TEXT_LIMIT, getTextLimit( combo ), null );
  }

  private static void renderListenSelection( Combo combo ) {
    renderListener( combo, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( combo ), false );
  }

  private static void renderListenModify( Combo combo ) {
    renderListener( combo, PROP_MODIFY_LISTENER, ModifyEvent.hasListener( combo ), false );
  }

  private static void renderListenVerify( Combo combo ) {
    renderListener( combo, PROP_VERIFY_LISTENER, VerifyEvent.hasListener( combo ), false );
  }

  //////////////////
  // Helping methods

  private static boolean isEditable( Combo combo ) {
    return ( ( combo.getStyle() & SWT.READ_ONLY ) == 0 );
  }

  private static int getItemHeight( Combo combo ) {
    int charHeight = Graphics.getCharHeight( combo.getFont() );
    int padding = 2 * LIST_ITEM_PADDING;
    return charHeight + padding;
  }

  private static Integer getTextLimit( Combo combo ) {
    Integer result = Integer.valueOf( combo.getTextLimit() );
    if( result.intValue() == Combo.LIMIT  ) {
      result = null;
    }
    return result;
  }
}
