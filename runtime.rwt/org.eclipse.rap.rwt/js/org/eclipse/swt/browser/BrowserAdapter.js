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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Browser", {

  factory : function( properties ) {
    var result = new org.eclipse.swt.browser.Browser();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "url",
    "functionResult"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "url" : function( widget, value ) {
      widget.setSource( value );
      widget.syncSource();
    },
    "functionResult" : function( widget, value ) {
      widget.setFunctionResult( value[ 0 ], value[ 1 ], value[ 2 ] );
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "progress"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : [
    "evaluate",
    "destroyFunctions",
    "createFunctions"
  ],
  
  methodHandler : {
    "evaluate" : function( widget, properties ) {
      widget.execute( properties.script );
    },
    "createFunctions" : function( widget, properties ) {
      var functions = properties.functions;
      for( var i = 0; i < functions.length; i++ ) {
        widget.createFunction( functions[ i ] );
      }
    },
    "destroyFunctions" : function( widget, properties ) {
      var functions = properties.functions;
      for( var i = 0; i < functions.length; i++ ) {
        widget.destroyFunction( functions[ i ] );
      }
    }
  }

} );