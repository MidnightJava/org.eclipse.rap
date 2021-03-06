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

/*global confirm: false*/

qx.Class.define( "org.eclipse.swt.Request", {
  type : "singleton",
  extend : qx.core.Target,

  construct : function() {
    this.base( arguments );
    // the URL to which the requests are sent
    this._url = "";
    // the map of parameters that will be posted with the next call to 'send()'
    this._parameters = {};
    // instance variables that hold the essential request parameters
    this._uiRootId = "";
    this._requestCounter = null;
    // Number of currently running or scheduled requests, used to determine when
    // to show the wait hint (e.g. hour-glass cursor)
    this._runningRequestCount = 0;
    // Flag that is set to true if send() was called but the delay timeout
    // has not yet timed out
    this._inDelayedSend = false;
    // As the CallBackRequests get blocked at the server to wait for
    // background activity I choose a large timeout...
    var requestQueue = qx.io.remote.RequestQueue.getInstance();
    requestQueue.setDefaultTimeout( 60000 * 60 * 24 ); // 24h
    // Initialize the request queue to allow only one request at a time
    requestQueue.setMaxConcurrentRequests( 1 );
    // References the currently running request or null if no request is active
    this._currentRequest = null;
  },

  destruct : function() {
    this._currentRequest = null;
  },

  events : {
    "send" : "qx.event.type.DataEvent",
    "received" : "qx.event.type.DataEvent"
  },

  members : {

    setUrl : function( url ) {
      this._url = url;
    },

    getUrl : function() {
      return this._url;
    },

    setUIRootId : function( uiRootId ) {
      this._uiRootId = uiRootId;
    },

    getUIRootId : function() {
      return this._uiRootId;
    },

    setRequestCounter : function( requestCounter ) {
      this._requestCounter = requestCounter;
    },

    getRequestCounter : function() {
      return this._requestCounter;
    },

    /**
     * Adds a request parameter to this request with the given name and value
     */
    addParameter : function( name, value ) {
      this._parameters[ name ] = value;
    },

    /**
     * Removes the parameter denoted by name from this request.
     */
    removeParameter : function( name ) {
      delete this._parameters[ name ];
    },

    /**
     * Returns the parameter value for the given name or null if no parameter
     * with such a name exists.
     */
    getParameter : function( name ) {
      var result = this._parameters[ name ];
      if( result === undefined ) {
        result = null;
      }
      return result;
    },

    /**
     * Adds the given eventType to this request. The sourceId denotes the id of
     * the widget that caused the event.
     */
    addEvent : function( eventType, sourceId ) {
      this._parameters[ eventType ] = sourceId;
    },

    /**
     * Sends this request asynchronously. All parameters that were added since
     * the last 'send()' will now be sent.
     */
    send : function() {
      if( !this._inDelayedSend ) {
        this._inDelayedSend = true;
        var func = function() {
          this._sendImmediate( true );
        };
        qx.client.Timer.once( func, this, 60 );
      }
    },

    sendSyncronous : function() {
      this._sendImmediate( false );
    },

    _sendImmediate : function( async ) {
      this._dispatchSendEvent();
      // set mandatory parameters; do this after regular params to override them
      // in case of conflict
      this._parameters[ "uiRoot" ] = this._uiRootId;
      if( this._requestCounter == -1 ) {
        // TODO [fappel]: This is a workaround that prevents sending a request
        // without a valid request id. Needed for background proccessing.
        this._inDelayedSend = false;
        this.send();
      } else {
        if( this._requestCounter != null ) {
          this._parameters[ "requestCounter" ] = this._requestCounter;
          this._requestCounter = -1;
        }
        // create and configure request object
        var request = this._createRequest();
        request.setAsynchronous( async );
        // copy the _parameters map which was filled during client interaction
        // to the request
        this._inDelayedSend = false;
        this._copyParameters( request );
        this._runningRequestCount++;
        // notify user when request takes longer than 500 ms
        if( this._runningRequestCount === 1 ) {
          qx.client.Timer.once( this._showWaitHint, this, 500 );
        }
        // clear the parameter list
        this._parameters = {};
        // queue request to be sent (async) or send and block (sync)
        if( async ) {
          request.send();
        } else {
          this._sendStandalone( request );
        }
      }
    },

    _copyParameters : function( request ) {
      var data = [];
      for( var parameterName in this._parameters ) {
        data.push(   encodeURIComponent( parameterName )
                   + "="
                   + encodeURIComponent( this._parameters[ parameterName ] ) );
      }
      request.setData( data.join( "&" ) );
    },

    _createRequest : function() {
      var result = new qx.io.remote.Request( this._url, "POST", "application/javascript" );
      result.addEventListener( "sending", this._handleSending, this );
      result.addEventListener( "completed", this._handleCompleted, this );
      result.addEventListener( "failed", this._handleFailed, this );
      return result;
    },

    _sendStandalone : function( request ) {
      // TODO [rh] WORKAROUND
      //      we would need two requestQueues (one for 'normal' requests that
      //      is limited to 1 concurrent request and one for the 'independant'
      //      requests created here
      //      Until qooxdoo supports multiple requestQueues we create and
      //      send this kind of request without knownledge of the request queue
      var vRequest = request;
      var vTransport = new qx.io.remote.Exchange(vRequest);
      // Establish event connection between qx.io.remote.Exchange instance and
      // qx.io.remote.Request
      vTransport.addEventListener( "sending", vRequest._onsending, vRequest );
      vTransport.addEventListener( "receiving", vRequest._onreceiving, vRequest );
      vTransport.addEventListener( "completed", vRequest._oncompleted, vRequest );
      vTransport.addEventListener( "aborted", vRequest._onaborted, vRequest );
      vTransport.addEventListener( "timeout", vRequest._ontimeout, vRequest );
      vTransport.addEventListener( "failed", vRequest._onfailed, vRequest );
      vTransport._start = ( new Date() ).valueOf();
      vTransport.send();
      // END WORKAROUND
    },

    ////////////////////////
    // Handle request events

    _handleSending : function( evt ) {
      var exchange = evt.getTarget();
      this._currentRequest = exchange.getRequest();
    },

    _handleFailed : function( evt ) {
      // [vt] workaround for bug #253756: Copied code from _handleSending since
      // the sending phase is skipped on failure in IE
      // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=253756
      var exchange = evt.getTarget();
      this._currentRequest = exchange.getRequest();
      var giveUp = true;
      if( this._isConnectionError( evt.getStatusCode() ) ) {
        giveUp = !this._handleConnectionError( evt );
      }
      if( giveUp ) {
        this._hideWaitHint();
        var request = exchange.getImplementation().getRequest();
        var text = request.responseText;
        // [if] typeof(..) == "unknown" is IE specific. Used to prevent error:
        // "The data  necessary to complete this operation is not yet available"
        if( typeof( text ) == "unknown" ) {
          text = undefined;
        }
        if( text && text.length > 0 ) {
          if( this._isJsonResponse( request ) ) {
            var messageObject = JSON.parse( text );
            org.eclipse.rwt.ErrorHandler.showErrorBox( messageObject.meta.message );
          } else {
            org.eclipse.rwt.ErrorHandler.showErrorPage( text );
          }
        } else {
          var statusCode = String( evt.getStatusCode() );
          text = "<p>Request failed.</p><pre>HTTP Status Code: " + statusCode + "</pre>";
          org.eclipse.rwt.ErrorHandler.showErrorPage( text );
        }
      }
      // [if] Dispose only finished transport - see bug 301261, 317616
      exchange.dispose();
    },

    _handleCompleted : function( evt ) {
      var exchange = evt.getTarget();
      var text = exchange.getImplementation().getRequest().responseText;
      var errorOccured = false;
      try {
        var messageObject = JSON.parse( text );
        org.eclipse.swt.EventUtil.setSuspended( true );
        org.eclipse.rwt.protocol.Processor.processMessage( messageObject );
        qx.ui.core.Widget.flushGlobalQueues();
        org.eclipse.swt.EventUtil.setSuspended( false );
        org.eclipse.rwt.UICallBack.getInstance().sendUICallBackRequest();
      } catch( ex ) {
        org.eclipse.rwt.ErrorHandler.processJavaScriptErrorInResponse( text,
                                                                       ex,
                                                                       this._currentRequest );
        errorOccured = true;
      }
      if( !errorOccured ) {
        this._dispatchReceivedEvent();
      }
      this._runningRequestCount--;
      this._hideWaitHint();
      // [if] Dispose only finished transport - see bug 301261, 317616
      exchange.dispose();
    },

    ///////////////////////////////
    // Handling connection problems

    _handleConnectionError : function( evt ) {
      var msg
        = "The server seems to be temporarily unavailable.\n"
        + "Would you like to retry?";
      var result = confirm( msg );
      if( result ) {
        var request = this._createRequest();
        var failedRequest = this._currentRequest;
        request.setAsynchronous( failedRequest.getAsynchronous() );
        // Reusing the same request object causes strange behaviour, therefore
        // create a new request and copy the relevant parts from the failed one
        var failedHeaders = failedRequest.getRequestHeaders();
        for( var headerName in failedHeaders ) {
          request.setRequestHeader( headerName, failedHeaders[ headerName ] );
        }
        var failedParameters = failedRequest.getParameters();
        for( var parameterName in failedParameters ) {
          request.setParameter( parameterName,
                                failedParameters[ parameterName ] );
        }
        request.setData( failedRequest.getData() );
        this._restartRequest( request );
      }
      return result;
    },

    _restartRequest : function( request ) {
      // TODO [rh] this is adapted from qx.io.remote.RequestQueue#add as there
      //      is no official way to insert a new request as the first one in
      //      RequestQueue
      request.setState( "queued" );
      var requestQueue = qx.io.remote.RequestQueue.getInstance();
      qx.lang.Array.insertAt( requestQueue._queue, request, 0 );
      requestQueue._check();
      if( requestQueue.getEnabled() ) {
        requestQueue._timer.start();
      }
    },

    _isConnectionError : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( statusCode ) {
        // for a description of the IE status codes, see
        // http://support.microsoft.com/kb/193625
        var result = (    statusCode === 12007    // ERROR_INTERNET_NAME_NOT_RESOLVED
                       || statusCode === 12029    // ERROR_INTERNET_CANNOT_CONNECT
                       || statusCode === 12030    // ERROR_INTERNET_CONNECTION_ABORTED
                       || statusCode === 12031    // ERROR_INTERNET_CONNECTION_RESET
                       || statusCode === 12152 ); // ERROR_HTTP_INVALID_SERVER_RESPONSE
        return result;
      },
      "gecko" : function( statusCode ) {
        // Firefox 3 reports other statusCode than oder versions (bug #249814)
        var result;
        // Check if Gecko > 1.9 is running (used in FF 3)
        // Gecko/app integration overview: http://developer.mozilla.org/en/Gecko
        if( org.eclipse.rwt.Client.getMajor() * 10 + org.eclipse.rwt.Client.getMinor() >= 19 ) {
          result = ( statusCode === 0 );
        } else {
          result = ( statusCode === -1 );
        }
        return result;
      },
      "default" : function( statusCode ) {
        return statusCode === 0;
      }
    } ),

    _isJsonResponse : function( request ) {
      var contentType = request.getResponseHeader( "Content-Type" );
      return contentType.indexOf( qx.util.Mime.JSON ) !== -1;
    },

    ///////////////////////////////////////////////////
    // Wait hint - UI feedback while request is running

    _showWaitHint : function() {
      if( this._runningRequestCount > 0 ) {
        var doc = qx.ui.core.ClientDocument.getInstance();
        doc.setGlobalCursor( qx.constant.Style.CURSOR_PROGRESS );
      }
    },

    _hideWaitHint : function() {
      if( this._runningRequestCount === 0 ) {
        var doc = qx.ui.core.ClientDocument.getInstance();
        doc.setGlobalCursor( null );
      }
    },

    _dispatchSendEvent : function() {
      if( this.hasEventListeners( "send" ) ) {
        var event = new qx.event.type.DataEvent( "send", this );
        this.dispatchEvent( event, true );
      }
    },

    _dispatchReceivedEvent : function() {
      if( this.hasEventListeners( "received" ) ) {
        var event = new qx.event.type.DataEvent( "received", this );
        this.dispatchEvent( event, true );
      }
    }
  }
});
