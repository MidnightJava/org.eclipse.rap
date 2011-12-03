/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.CTabFolder", {

  factory : function( properties ) {
    var result = new org.eclipse.swt.custom.CTabFolder();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    result.setTabPosition( properties.style.indexOf( "BOTTOM" ) != -1 ? "bottom" : "top" );
    org.eclipse.swt.custom.CTabFolder.setToolTipTexts.apply( result, properties.toolTipTexts );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "tabPosition",
    "tabHeight",
    "minMaxState",
    "minimizeBounds",
    "minimizeVisible",
    "maximizeBounds",
    "maximizeVisible",
    "chevronBounds",
    "chevronVisible",
    "unselectedCloseVisible",
    "selection",
    "selectionBackground",
    "selectionForeground",
    "selectionBackgroundImage",
    "selectionBackgroundGradient",
    "borderVisible"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "minimizeBounds" : function( widget, value ) {
      widget.setMinButtonBounds.apply( widget, value );
    },
    "minimizeVisible" : function( widget, value ) {
      if( value ) {
        widget.showMinButton();
      } else {
        widget.hideMinButton();
      }
    },
    "maximizeBounds" : function( widget, value ) {
      widget.setMaxButtonBounds.apply( widget, value );
    },
    "maximizeVisible" : function( widget, value ) {
      if( value ) {
        widget.showMaxButton();
      } else {
        widget.hideMaxButton();
      }
    },
    "chevronBounds" : function( widget, value ) {
      widget.setChevronBounds.apply( widget, value );
    },
    "chevronVisible" : function( widget, value ) {
      if( value ) {
        widget.showChevron();
      } else {
        widget.hideChevron();
      }
    },
    "selection" : function( widget, value ) {
      widget.deselectAll();
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( item ) {
        if( item != null ) {
          item.setSelected( true );
        }
      } );
    },
    "selectionBackground" : function( widget, value ) {
      if( value === null ) {
        widget.setSelectionBackground( null );
      } else {
        widget.setSelectionBackground( qx.util.ColorUtil.rgbToRgbString( value ) );
      }
    },
    "selectionForeground" : function( widget, value ) {
      if( value === null ) {
        widget.setSelectionForeground( null );
      } else {
        widget.setSelectionForeground( qx.util.ColorUtil.rgbToRgbString( value ) );
      }
    },
    "selectionBackgroundGradient" : function( widget, value ) {
      if( value === null ) {
        widget.setSelectionBackgroundGradient( null, null, true );
      } else {
        widget.setSelectionBackgroundGradient( value[ 0 ], value[ 1 ], value[ 2 ] );
      }
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "folder",
    "selection"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );