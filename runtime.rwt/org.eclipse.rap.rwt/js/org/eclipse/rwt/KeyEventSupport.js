/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.KeyEventSupport", {
  type : "singleton",
  extend : qx.core.Object,

  construct : function() {
    this.base( arguments );
    org.eclipse.rwt.EventHandler.setKeyDomEventFilter( this._onKeyDomEvent, this );
    org.eclipse.rwt.EventHandler.setKeyEventFilter( this._onKeyEvent, this );
    this._keyBindings = {};
    this._cancelKeys = {};
    this._currentKeyCode = -1;
    this._bufferedEvents = [];
    this._keyEventRequestRunning = false;
    this._ignoreNextKeypress = false;
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "received", this._onRequestReceived, this );
  },

  destruct : function() {
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "received", this._onRequestReceived, this );
  },

  members : {
    
    //////
    // API

    setKeyBindings : function( value ) {
      this._keyBindings = value;
    },

    setCancelKeys : function( value ) {
      this._cancelKeys = value;
    },
    
    ////////////
    // Internals
    
    _onKeyDomEvent : function( eventType, keyCode, charCode, domEvent ) {
      if( eventType === "keydown" ) {
        this._currentKeyCode = keyCode;
      }
      var control = this._getTargetControl();
      if( this._shouldCancel( this._currentKeyCode, charCode, domEvent, control ) ) {
        org.eclipse.rwt.EventHandlerUtil.stopDomEvent( domEvent );
        domEvent._noProcess = true;
      }
    },

    _onKeyEvent : function( eventType, keyCode, charCode, domEvent ) {
      var control = this._getTargetControl();
      if( this._shouldSend( eventType, this._currentKeyCode, charCode, domEvent, control ) ) {
        this._sendKeyEvent( control, this._currentKeyCode, charCode, domEvent );
      }
      if( eventType === "keypress" || eventType === "keyup" ) {
        this._ignoreNextKeypress = false;
      }
      return !domEvent._noProcess;
    },
    
    /////////////
    // send event

    _shouldSend : function( eventType, keyCode, charCode, domEvent, control ) {
      var result = false;
      if( this._isRelevant( keyCode, eventType, domEvent ) ) {
        if( this._hasTraverseListener( control ) && this._isTraverseKey( keyCode ) ) {
          result = true;
        } 
        if( !result && this._hasKeyListener( control ) ) {
          var activeKeys = control.getUserData( "activeKeys" );
          if( activeKeys ) {
            result = this._isActive( activeKeys, domEvent, keyCode, charCode );
          } else {
            result = true;
          }
        }
        if( !result ) {
          result = this._isActive( this._keyBindings, domEvent, keyCode, charCode );
        }
      }
      return result;
    },
    
    _isRelevant : function( keyCode, eventType, domEvent ) {
      var result;
      if( eventType === "keypress" ) {
        // NOTE : modifier don't repeat
        result = !this._isModifier( keyCode ) && !this._ignoreNextKeypress;
      } else if( eventType === "keydown" ) {
        // NOTE : Prefered when keypress might not be fired, e.g. browser shortcuts that
        //        are not or can not be prevented/canceled. Key might not repeat in that case.
        //        Not to be used when charcode might be unkown (e.g. shift + char, special char)-
        var EventHandlerUtil = org.eclipse.rwt.EventHandlerUtil;
        var result =    EventHandlerUtil.isNonPrintableKeyCode( keyCode )
                     || EventHandlerUtil.isSpecialKeyCode( keyCode );
        if( !result && ( domEvent.altKey || domEvent.ctrlKey ) ) {
          result = this._isAlphaNumeric( keyCode );
        }
        if( result ) {
          this._ignoreNextKeypress = true;
        }
      }
      if( domEvent.ctrlKey && keyCode === 9 ) {
        // Used by the browser to switch tabs, not useable
        result = false;
      }
      return result;
    },

    _onRequestReceived : function( evt ) {
      if( this._keyEventRequestRunning ) {
        this._keyEventRequestRunning = false;
        this._checkBufferedEvents();
      }
    },

    _checkBufferedEvents : function() {
      while( this._bufferedEvents.length > 0 && !this._keyEventRequestRunning ) {
        var size = this._bufferedEvents.length;
        var oldEvent = this._bufferedEvents.shift();
        this._sendKeyEvent.apply( this, oldEvent );
      }
    },

    _sendKeyEvent : function( widget, keyCode, charCode, domEvent ) {
      if( this._keyEventRequestRunning ) {
        this._bufferedEvents.push( [ widget, keyCode, charCode, domEvent ] );
      } else {
        this._attachKeyEvent( widget, keyCode, charCode, domEvent );
        this._keyEventRequestRunning = true;
        this._sendRequestAsync();
      }
    },
    
    _sendRequestAsync : function() {
      window.setTimeout( function() { 
        org.eclipse.swt.Request.getInstance()._sendImmediate( true );
      }, 0 );
    },

    _attachKeyEvent : function( widget, keyCode, charCode, domEvent ) {
      var req = org.eclipse.swt.Request.getInstance();
      var id;
      if( widget === null ) {
        id = "w1";
      } else {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        id = wm.findIdByWidget( widget );
      }
      var finalCharCode = this._getCharCode( keyCode, charCode, domEvent );
      req.addEvent( "org.eclipse.swt.events.keyDown", id );
      req.addParameter( "org.eclipse.swt.events.keyDown.keyCode", keyCode );
      req.addParameter( "org.eclipse.swt.events.keyDown.charCode", finalCharCode );
      var modifier = "";
      var commandKey = org.eclipse.rwt.Client.getPlatform() === "mac" && domEvent.metaKey;
      if( domEvent.shiftKey ) {
        modifier += "shift,";
      }
      if( domEvent.ctrlKey || commandKey ) {
        modifier += "ctrl,";
      }
      if( domEvent.altKey ) {
        modifier += "alt,";
      }
      req.addParameter( "org.eclipse.swt.events.keyDown.modifier", modifier );
    },
    
    ///////////////
    // cancel event

    _shouldCancel : function( keyCode, charCode, domEvent, control ) {
      var result = this._isActive( this._cancelKeys, domEvent, keyCode, charCode );
      if( !result ) { 
        var cancelKeys = control ? control.getUserData( "cancelKeys" ) : null;
        if( cancelKeys ) {
          result = this._isActive( cancelKeys, domEvent, keyCode, charCode );
        }
      }
      return result;
    },

    /////////
    // helper

    _getTargetControl : function() {
      var result = org.eclipse.rwt.EventHandler.getCaptureWidget();
      if( !result ) {
        var focusRoot = org.eclipse.rwt.EventHandler.getFocusRoot();
        result = focusRoot === null ? null : focusRoot.getActiveChild();
      }
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      while( result !== null && !widgetManager.isControl( result ) ) {
        result = result.getParent ? result.getParent() : null;
      }
      return result;
    },

    _isActive : function( activeKeys, domEvent, keyCode, charCode ) {
      var result = false;
      var identifier = this._getKeyBindingIdentifier( domEvent, "keydown", keyCode, charCode );
      result = activeKeys[ identifier ] === true;
      if( !result ) {
        identifier = this._getKeyBindingIdentifier( domEvent, "keypress", keyCode, charCode );
        result = activeKeys[ identifier ] === true;
      }
      return result;
    },
    
    _getKeyBindingIdentifier : function( domEvent, eventType, keyCode, charCode ) {
      var result = [];
      if( eventType === "keydown" && !isNaN( keyCode ) && keyCode > 0 ) {
        if( domEvent.altKey ) {
          result.push( "ALT" );
        }
        if( domEvent.ctrlKey ) {
          result.push( "CTRL" ); //TODO Command @ apple?
        }
        if( domEvent.shiftKey ) {
          result.push( "SHIFT" );
        }
        result.push( "#" + keyCode.toString() );
      } else if( eventType === "keypress" && !isNaN( charCode ) && charCode > 0 ) {
        result.push( String.fromCharCode( charCode ) );
      }
      return result.join( "+" );
    },
    
    _getCharCode : function( keyCode, charCode, domEvent ) {
      var result = charCode;
      if( result === 0 && this._isAlphaNumeric( keyCode ) ) {
        if( domEvent.shiftKey && !this._isNumeric( keyCode ) ) {
          result = keyCode;
        } else {
          result = String.fromCharCode( keyCode ).toLowerCase().charCodeAt( 0 );
        }
      }
      return result;
    },
    
    _isModifier : function( keyCode ) {
      return keyCode >= 16 && keyCode <= 20 && keyCode !== 19;
    },
    
    _isAlphaNumeric : function( keyCode ) {
      return ( keyCode >= 65 && keyCode <= 90 ) || this._isNumeric( keyCode );
    },
    
    _isNumeric : function( keyCode ) {
      return keyCode >= 48 && keyCode <= 57;
    },

    _hasKeyListener : function( widget ) {
      return widget !== null && widget.getUserData( "keyListener" ) === true;
    },

    _hasTraverseListener : function( widget ) {
      return widget !== null && widget.getUserData( "traverseListener" ) === true;
    },

    _isTraverseKey : function( keyCode ) {
      var result = false;
      if( keyCode === 27 || keyCode === 13 || keyCode === 9 ) {
        result = true;
      }
      return result;
    }

  }
} );

// force instance:
org.eclipse.rwt.KeyEventSupport.getInstance();