/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "org.eclipse.rwt.protocol" );
 
org.eclipse.rwt.protocol.Processor = {

  processMessage : function( messageObject ) {
    this.processMeta( messageObject.meta );
    var operations = messageObject.operations;
    for( var i = 0; i < operations.length; i++ ) {
      this.processOperation( operations[ i ] );
    }
  },

  processMeta : function( meta ) {
    if( meta.requestCounter !== undefined ) {
      var req = org.eclipse.swt.Request.getInstance();
      req.setRequestCounter( meta.requestCounter );
    }
  },

  processOperation : function( operation ) {
    try {
      switch( operation.action ) {
        case "create":
          this._processCreate( operation.target, operation.type, operation.properties );
        break;
        case "set":
          this._processSet( operation.target, operation.properties );
        break; 
        case "destroy":
          this._processDestroy( operation.target );
        break; 
        case "call":
          this._processCall( operation.target, operation.method, operation.properties );
        break; 
        case "listen":
          this._processListen( operation.target, operation.properties );
        break; 
      }
    } catch( ex ) {
      this._processError( ex, operation );
    }
  },

  ////////////
  // Internals

  _processCreate : function( targetId, type, properties ) {
    var adapter = org.eclipse.rwt.protocol.AdapterRegistry.getAdapter( type );
    var targetObject = adapter.factory( properties );
    this._addTarget( targetObject, targetId, adapter );
    this._processSetImpl( targetObject, adapter, properties );
  },

  _processDestroy : function( targetId ) {
    var objectEntry = org.eclipse.rwt.protocol.ObjectManager.getEntry( targetId );
    var adapter = objectEntry.adapter;
    var targetObject = objectEntry.object;
    if( adapter.destructor ) {
      adapter.destructor( targetObject );
    }
    org.eclipse.rwt.protocol.ObjectManager.remove( targetId );
  },

  _processSet : function( targetId, properties ) {
    var objectEntry = org.eclipse.rwt.protocol.ObjectManager.getEntry( targetId );
    this._processSetImpl( objectEntry.object, objectEntry.adapter, properties );
  },
  
  _processSetImpl : function( targetObject, adapter, properties ) {
    if( properties && adapter.properties  instanceof Array ) {
      for( var i = 0; i < adapter.properties.length; i++ ) {
        var property = adapter.properties [ i ];
        var value = properties[ property ];
        if( value !== undefined ) {
          if( adapter.propertyHandler && adapter.propertyHandler[ property ] ) {
            adapter.propertyHandler[ property ].call( window, targetObject, value );
          } else {
            var setterName = this._getSetterName( property );
            targetObject[ setterName ]( value );
          }
        }
      }
    }
  },

  _processCall : function( targetId, method, properties ) {
    var objectEntry = org.eclipse.rwt.protocol.ObjectManager.getEntry( targetId );
    var adapter = objectEntry.adapter;
    var targetObject = objectEntry.object;
    if( adapter.methods instanceof Array && adapter.methods.indexOf( method ) !== -1 ) {
      if( adapter.methodHandler && adapter.methodHandler[ method ] ) {
        adapter.methodHandler[ method ]( targetObject, properties );
      } else {
        targetObject[ method ]( properties );
      }
    }
  },

  _processListen : function( targetId, properties ) {
    var objectEntry = org.eclipse.rwt.protocol.ObjectManager.getEntry( targetId );
    var adapter = objectEntry.adapter;
    var targetObject = objectEntry.object;
    if( adapter.listeners instanceof Array ) {
      for( var i = 0; i < adapter.listeners.length; i++ ) {
        var type = adapter.listeners[ i ];
        if( properties[ type ] === true ) {
          this._addListener( adapter, targetObject, type );
        } if( properties[ type ] === false ) {
          this._removeListener( adapter, targetObject, type );            
        }
      }
    }
  },

  ////////////
  // Internals

  _processError : function( error, operation ) {
    var errorstr; 
    if( error ) {
      errorstr = error.message ? error.message : error.toString();
    } else {
      errorstr = "No Error given!";
    }
    var msg = "Operation \"" + operation.action + "\"";
    msg += " on target \"" +  operation.target + "\"";
    var objectEntry = org.eclipse.rwt.protocol.ObjectManager.getEntry( operation.target );
    var target = objectEntry ? objectEntry.object : null;
    msg += " of type \"" +  ( target && target.classname ? target.classname : target ) + "\"";
    msg += " failed:";
    msg += "\n" + errorstr +"\n";
    msg += "Properties: \n" + this._getPropertiesString( operation );
    throw new Error( msg );
  },
  
  _getPropertiesString : function( operation ) {
    var result = "";
    for( var key in operation.properties ) {
      result += key + " = " + operation.properties[ key ] + "\n";
    }
    return result;
  },

  _addTarget : function( target, targetId, adapter ) {
    if( target instanceof qx.ui.core.Widget ) {
      // TODO [tb] : remove WidgetManager and then this if 
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( target, targetId, false, adapter ); // uses ObjectManager internally
    } else {
      org.eclipse.rwt.protocol.ObjectManager.add( targetId, target, adapter );
    }
  },

  _addListener : function( adapter, targetObject, eventType ) {
    if( adapter.listenerHandler &&  adapter.listenerHandler[ eventType ] ) {
      adapter.listenerHandler[ eventType ]( targetObject, true );
    } else {
      var setterName = this._getListenerSetterName( eventType );
      targetObject[ setterName ]( true );
    }
  },

  _removeListener : function( adapter, targetObject, eventType ) {
    if( adapter.listenerHandler &&  adapter.listenerHandler[ eventType ] ) {
      adapter.listenerHandler[ eventType ]( targetObject, false );
    } else {
      var setterName = this._getListenerSetterName( eventType );
      targetObject[ setterName ]( false );
    }
  },

  _getSetterName : function( property ) {
    return "set" + qx.lang.String.toFirstUp( property );
  },

  _getListenerSetterName : function( eventType ) {
    return "setHas" + qx.lang.String.toFirstUp( eventType ) + "Listener";
  }

};
