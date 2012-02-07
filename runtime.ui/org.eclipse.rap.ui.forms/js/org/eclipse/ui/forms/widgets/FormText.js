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

qx.Class.define( "org.eclipse.ui.forms.widgets.FormText", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "formtext" );
    this._hyperlinks = [];
    this._segments = [];
    this._hyperlinkMode = org.eclipse.ui.forms.widgets.FormText.UNDERLINE_ALWAYS;
    this._hyperlinkForeground = "#0000FF";
    this._hyperlinkActiveForeground = "#0000FF";
  },

  destruct : function() {
    this.clearContent();
  },

  statics : {
    UNDERLINE_NEVER : 1,
    UNDERLINE_HOVER : 2,
    UNDERLINE_ALWAYS : 3
  },

  members : {

    createBullet : function( style, image, text, bounds ) {
      var bullet = new qx.ui.basic.Atom();
      bullet.setAppearance( "formtext-bullet" );
      switch( style ) {
        case 2:
          bullet.setLabel( text );
          break;
        case 3:
          bullet.setIcon( image );
          break;
        default:
          bullet.setIcon( image );
      }
      bullet.set( {
        left   : bounds[ 0 ],
        top    : bounds[ 1 ],
        width  : bounds[ 2 ],
        height : bounds[ 3 ]
      } );
      this._segments[ this._segments.length ] = bullet;
      this.add( bullet );
    },

    createTextHyperlinkSegment : function( text, toolTip, bounds, font ) {
      var textHyperlink = new qx.ui.basic.Label();
      textHyperlink.setAppearance( "formtext-hyperlink" );
      var escapedText = this._escapeText( text );
      textHyperlink.set( {
        text   : escapedText,
        left   : bounds[ 0 ],
        top    : bounds[ 1 ],
        width  : bounds[ 2 ],
        height : bounds[ 3 ]
      } );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.setToolTip( textHyperlink, toolTip );
      if( font != null ) {
        widgetManager.setFont( textHyperlink, font[ 0 ], font[ 1 ], font[ 2 ], font[ 3 ] );
      }
      textHyperlink.addEventListener( "mousemove", this._onMouseMove, this );
      textHyperlink.addEventListener( "mouseout", this._onMouseOut, this );
      this._hyperlinks[ this._hyperlinks.length ] = textHyperlink;
      this.add( textHyperlink );
    },

    createTextSegment : function( text, bounds, font, color ) {
      var textFragment = new qx.ui.basic.Label();
      textFragment.setAppearance( "formtext-text" );
      var escapedText = this._escapeText( text );
      textFragment.set( {
        text   : escapedText,
        left   : bounds[ 0 ],
        top    : bounds[ 1 ],
        width  : bounds[ 2 ],
        height : bounds[ 3 ]
      } );
      if( font != null ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        widgetManager.setFont( textFragment, font[ 0 ], font[ 1 ], font[ 2 ], font[ 3 ] );
      }
      if( color != null ) {
        textFragment.setTextColor( qx.util.ColorUtil.rgbToRgbString( color ) );
      }
      this._segments[ this._segments.length ] = textFragment;
      this.add( textFragment );
    },

    createImageHyperlinkSegment : function( source, toolTip, bounds ) {
      var imageHyperlink = new qx.ui.basic.Image();
      imageHyperlink.setAppearance( "formtext-hyperlink" );
      imageHyperlink.set( {
        source     : source,
        left       : bounds[ 0 ],
        top        : bounds[ 1 ],
        width      : bounds[ 2 ],
        height     : bounds[ 3 ],
        paddingTop : 2
      } );
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.setToolTip( imageHyperlink, toolTip );
      this._segments[ this._segments.length ] = imageHyperlink;
      this.add( imageHyperlink );
    },

    createImageSegment : function( source, bounds ) {
      var image = new qx.ui.basic.Image();
      image.setAppearance( "formtext-image" );
      image.set( {
        source : source,
        left   : bounds[ 0 ],
        top    : bounds[ 1 ],
        width  : bounds[ 2 ],
        height : bounds[ 3 ]
      } );
      this._segments[ this._segments.length ] = image;
      this.add( image );
    },

    setHyperlinkSettings : function( mode, foreground, activeForeground ) {
      this._hyperlinkMode = mode;
      if( foreground != null ) {
        this._hyperlinkForeground = foreground;
      }
      if( activeForeground != null ) {
        this._hyperlinkActiveForeground = activeForeground;
      }
      this.updateHyperlinks();
    },

    updateHyperlinks : function() {
      for( var i = 0; i < this._hyperlinks.length; i++ ) {
        this._hyperlinks[ i ].setTextColor( this._hyperlinkForeground );
        if( this._hyperlinkMode == org.eclipse.ui.forms.widgets.FormText.UNDERLINE_ALWAYS ) {
          this._hyperlinks[ i ].setStyleProperty( "textDecoration", "underline");
        }
      }
    },

    clearContent : function() {
      for( var i = 0; i < this._hyperlinks.length; i++ ) {
        this._hyperlinks[ i ].removeEventListener( "mousemove",
                                                   this._onMouseMove,
                                                   this );
        this._hyperlinks[ i ].removeEventListener( "mouseout",
                                                   this._onMouseOut,
                                                   this );
        this._hyperlinks[ i ].destroy();
      }
      for( var i = 0; i < this._segments.length; i++ ) {
        this._segments[ i ].destroy();
      }
      this._hyperlinks = [];
      this._segments = [];
    },

    _escapeText : function( text ) {
      return text.replace( / /g, "&nbsp;" );
    },

    _onMouseMove : function( evt ) {
      var hyperlink = evt.getTarget();
      if( !this.hasState( "hover" ) ) {
        this.addState( "hover" );
        hyperlink.setTextColor( this._hyperlinkActiveForeground );
        if( this._hyperlinkMode == org.eclipse.ui.forms.widgets.FormText.UNDERLINE_HOVER ) {
          hyperlink.setStyleProperty( "textDecoration", "underline");
        }
      }
    },

    _onMouseOut : function( evt ) {
      var hyperlink = evt.getTarget();
      if( this.hasState( "hover" ) ) {
        this.removeState( "hover" );
        hyperlink.setTextColor( this._hyperlinkForeground );
        if( this._hyperlinkMode == org.eclipse.ui.forms.widgets.FormText.UNDERLINE_HOVER ) {
          hyperlink.setStyleProperty( "textDecoration", "none");
        }
      }
    }
  }
} );
