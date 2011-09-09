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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Spinner", {

  factory : function( properties ) {
    var result = new org.eclipse.swt.widgets.Spinner();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    if( styleMap.READ_ONLY ) {
      result.setEditable( false );
    }
    if( styleMap.WRAP ) {
      result.setWrap( true );
    }
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    // [if] Important: Order matters - minimum, maximum, selection
    "minimum",
    "maximum",
    "selection",
    "digits",
    "increment",
    "pageIncrement",
    "textLimit",
    "decimalSeparator"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "minimum" : function( widget, value ) {
      // [if] Ensures that we don't set min bigger than current max, otherwize and error will be
      // thrown. The correct max is always set by the server in this case.
      var max = widget.getMax();
      if( value > max ) {
        widget.setMax( value + 1 );
      }
      widget.setMin( value );
    },
    "maximum" : function( widget, value ) {
      widget.setMax( value );
    },
    "selection" : function( widget, value ) {
      widget.setValue( value );
    },
    "increment" : function( widget, value ) {
      widget.setIncrementAmount( value );
      widget.setWheelIncrementAmount( value );
    },
    "pageIncrement" : function( widget, value ) {
      widget.setPageIncrementAmount( value );
    },
    "textLimit" : function( widget, value ) {
      widget.setMaxLength( value );
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "modify",
    "selection"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );