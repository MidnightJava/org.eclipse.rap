/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.graphics.GC", {

  extend : qx.core.Object,

  construct : function( control ) {
    this.base( arguments );
    this._control = control;
    this._control.addEventListener( "create", this._onControlCreate, this );
    this._vmlCanvas = null;
    this._canvas = null;
    this._context = null;
    this._createCanvas();
    this._textCanvas = document.createElement( "div" );
    this._textCanvas.style.position = "absolute";
    this._textCanvas.style.overflow = "hidden";
    this._textCanvas.style.left = "0px";
    this._textCanvas.style.top = "0px";
    if( this._control.isCreated() ) {
      this._addCanvasToDOM();
    }
    this._linearGradient = null;
  },

  destruct : function() {
    this._control.removeEventListener( "create", this._onControlCreate, this );
    if( org.eclipse.rwt.Client.isMshtml() ) {
      this._control.removeEventListener( "insertDom", this._onCanvasAppear, this );
    }
    if( this._control.isCreated() && !this._control.isDisposed() ) {
      this._removeCanvasFromDOM();
    }
    this._control = null;
    this._canvas = null;
    if( this._context.dispose ) {
      this._context.dispose();
    }
    this._context = null;
    this._textCanvas = null;
  },

  members : {

    init : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( width, height, font, background, foreground ) {
        // TODO [tb]: Should the control be detached from the DOM 
        // (e.g. by Widget.prepareEnhancedBorder), this might lead to glitches 
        // in IE/VML. The flush prevents this in some cases:
        qx.ui.core.Widget.flushGlobalQueues();
        this._initTextCanvas( width, height );
        this._context.clearRect( 0, 0, width, height );
        this._initFields( font, background, foreground );
      },
      "default" : function( width, height, font, background, foreground  ) {
        this._initTextCanvas( width, height );
        this._canvas.width = width;
        this._canvas.style.width = width + "px";
        this._canvas.height = height;
        this._canvas.style.height = height + "px";
        this._context.clearRect( 0, 0, width, height );
        this._initFields( font, background, foreground );
      }
    } ),


    /**
     * Executes drawing operations using the HTML5-Canvas 2D-Context syntax.
     * Only a subset is supported on all browser, espcially IE is limited.
     * Each operation is an array starting with the name of the function to call, followed
     * by its parameters. Properties are treated the same way, i.e. [ "propertyName", "value" ].
     * Other differences from official HTML5-Canvas API: 
     *  - Colors are to be given as array ( [ red, green blue ] )
     *  - "addColorStop" will automatically applied to the last created gradient.
     *  - To assign the last created linear gradient as a style, use "linearGradient" as the value.
     *  - strokeText behaves like fillText and fillText draws a rectangular background
     *  - "arc" is differing from the HTML5-Canvas implementation in that it takes two radii (x,y).
     *    This might be fixed if transformations are supported in IE (like exCanvas does).
     */    
    draw : function( operations ) {
      for( var i = 0; i < operations.length; i++ ) {
        try {
          var op = operations[ i ][ 0 ];
          switch( op ) {
            case "fillStyle":
            case "strokeStyle": 
            case "globalAlpha": 
            case "lineWidth": 
            case "lineCap": 
            case "lineJoin": 
            case "font": 
              this._setProperty( operations[ i ] );
            break;
            case "createLinearGradient": 
            case "addColorStop": 
            case "fillText": 
            case "strokeText": 
            case "arc":
            case "drawImage":
              this[ "_" + op ]( operations[ i ] );
            break;
            default:
              this._context[ op ].apply( this._context, operations[ i ].slice( 1 ) );
            break;
          }
        } catch( ex ) {
          var opArrStr = "[ " + operations[ i ].join( ", " ) + " ]"; 
          throw new Error( "Drawing operation failed: " + opArrStr + " :" + ex.message );
        }
      }
    },
    
    ////////////
    // Internals

    _createCanvas : qx.core.Variant.select( "qx.client", {
      "mshtml" : function() {
        this._vmlCanvas = org.eclipse.rwt.VML.createCanvas();
        this._canvas = org.eclipse.rwt.VML.getCanvasNode( this._vmlCanvas );
        this._context = new org.eclipse.rwt.VMLCanvas( this._vmlCanvas );
        this._control.addEventListener( "insertDom", this._onCanvasAppear, this );
      },
      "default" : function() {
        this._canvas = document.createElement( "canvas" );
        this._context = this._canvas.getContext( "2d" );
      }
    } ),

    _onControlCreate : function() {
      this._addCanvasToDOM();
    },

    _onCanvasAppear : function() {
      var graphicsUtil = org.eclipse.rwt.GraphicsUtil;
      graphicsUtil.handleAppear( this._vmlCanvas );
    },

    _addCanvasToDOM  : function() {
      // TODO [tb] : append textCanvas onDemand
      var controlElement = this._control._getTargetNode();
      var firstChild = controlElement.firstChild;
      if( firstChild ) {
        controlElement.insertBefore( this._canvas, firstChild );
        controlElement.insertBefore( this._textCanvas, firstChild );
      } else {
        controlElement.appendChild( this._canvas );
        controlElement.appendChild( this._textCanvas );
      }
    },

    _removeCanvasFromDOM : function() {
      var controlElement = this._control._getTargetNode();
      this._canvas.parentNode.removeChild( this._canvas );
      this._textCanvas.parentNode.removeChild( this._textCanvas );
    },

    _initTextCanvas : function( width, height ) {
      this._textCanvas.width = width;
      this._textCanvas.style.width = width + "px";
      this._textCanvas.height = height;
      this._textCanvas.style.height = height + "px";
      this._textCanvas.innerHTML = "";
    },

    _initFields : function( font, background, foreground ) {
      this._context.strokeStyle = qx.util.ColorUtil.rgbToRgbString( foreground );
      this._context.fillStyle = qx.util.ColorUtil.rgbToRgbString( background );
      this._context.globalAlpha = 1.0;
      this._context.lineWidth = 1;
      this._context.lineCap = "butt";
      this._context.lineJoin = "miter";
      this._context.font = font;
    },

    _arc : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( operation ) {
        this._context[ operation[ 0 ] ].apply( this._context, operation.slice( 1 ) );
      }, 
      "default" : function( operation ) {
        var cx = operation[ 1 ];
        var cy = operation[ 2 ];
        var rx = operation[ 3 ];
        var ry = operation[ 4 ];
        var startAngle = operation[ 5 ];
        var endAngle = operation[ 6 ];
        var dir = operation[ 7 ];
        if( rx > 0 && ry > 0 ) {
          this._context.save();
          this._context.translate( cx, cy );
          // TODO [tb] : using scale here changes the stroke-width also, looks wrong
          this._context.scale( 1, ry / rx );
          this._context.arc( 0, 0, rx, startAngle, endAngle, dir );
          this._context.restore();
        }
      }
    } ),
    
    _setProperty : function( operation ) {
      var property = operation[ 0 ];
      var value = operation[ 1 ];
      if( value === "linearGradient" ) {
        value = this._linearGradient;
      } else if( value instanceof Array ) {
        value = qx.util.ColorUtil.rgbToRgbString( value );
      }
       this._context[ property ] = value;
    },

    _strokeText : function( operation ) {
      this._fillText( operation );
    },

    _fillText : function( operation ) {
      var fill = operation[ 0 ] === "fillText";
      var text = this._escapeText( operation[ 1 ], operation[ 2 ], operation[ 3 ], operation[ 4 ] );
      var x = operation[ 5 ];
      var y = operation[ 6 ];
      var textElement = document.createElement( "div" );
      var style = textElement.style;
      style.position = "absolute";
      style.left = x + "px";
      style.top = y + "px";
      style.color = this._context.strokeStyle;
      if( fill ) {
        style.backgroundColor = this._context.fillStyle;
      }
      if( this._context.font != "" ) {
        style.font = this._context.font;
      }
      textElement.innerHTML = text;
      this._textCanvas.appendChild( textElement );
    },

    _escapeText : function( value, drawMnemonic, drawDelemiter, drawTab ) {
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, drawMnemonic );
      var replacement = drawDelemiter ? "<br/>" : "";
      text = EncodingUtil.replaceNewLines( text, replacement );
      replacement = drawTab ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "";
      text = text.replace( /\t/g, replacement );
      return text;
    },

    _drawImage : function( operation ) {
      var args = operation.slice( 1 );
      var simple = args.length === 3;
      var image = new Image();
      image.src = args[ 0 ];
      args[ 0 ] = image;
      // On (native) canvas, only loaded images can be drawn: 
      if( image.complete || org.eclipse.rwt.Client.isMshtml() ) {
        this._context.drawImage.apply( this._context, args );
      } else {
        var alpha = this._context.globalAlpha;
        var context = this._context;
        image.onload = function() {
          // TODO [tb] : The z-order will be wrong in this case.
          context.save();
          context.globalAlpha = alpha;
          context.drawImage.apply( context, args );
          context.restore();
        };
      }
    },

    _createLinearGradient : function( operation ) {
      var func = this._context.createLinearGradient;
      this._linearGradient = func.apply( this._context, operation.slice( 1 ) );
    },
    
    _addColorStop : function( operation ) {
      this._linearGradient.addColorStop( 
        operation[ 1 ],
        qx.util.ColorUtil.rgbToRgbString( operation[ 2 ] ) 
      );
    }

  }
} );