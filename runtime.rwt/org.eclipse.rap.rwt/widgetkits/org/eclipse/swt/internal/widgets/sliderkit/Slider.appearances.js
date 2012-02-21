/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
var appearances = {
// BEGIN TEMPLATE //

  "slider" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        border : tv.getCssBorder( "Slider", "border" ),
        font : tv.getCssFont( "*", "font" ),
        textColor : tv.getCssColor( "*", "color" ),
        backgroundColor : tv.getCssColor( "Slider", "background-color" )
      };
    }
  },

  "slider-thumb" : {
    include : "atom",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.backgroundColor = tv.getCssColor( "Slider-Thumb", "background-color" );
      result.border = tv.getCssBorder( "Slider-Thumb", "border" );
      result.backgroundImage = tv.getCssImage( "Slider-Thumb", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "Slider-Thumb", "background-image" );
      return result;
    }
  },

  "slider-min-button" : {
    include : "atom",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.font = tv.getCssFont( "Button", "font" );
      result.textColor = tv.getCssColor( "Button", "color" );
      result.spacing = tv.getCssDimension( "Button", "spacing" );
      result.padding = tv.getCssBoxDimensions( "Slider-DownButton", "padding" );
      result.backgroundColor = tv.getCssColor( "Slider-DownButton", "background-color" );
      result.icon = tv.getCssSizedImage( "Slider-DownButton-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssSizedImage( "Slider-DownButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "Slider-DownButton", "background-image" );
      }
      result.backgroundGradient = tv.getCssGradient( "Slider-DownButton", "background-image" );
      result.border = tv.getCssBorder( "Slider-DownButton", "border" );
      if( states[ "rwt_HORIZONTAL" ] ){
        result.width = 16;
      } else {
        result.height = 16;
      }
      result.cursor = tv.getCssCursor( "Slider-DownButton", "cursor" );
      return result;
    }
  },

  "slider-max-button" : {
    include : "atom",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.font = tv.getCssFont( "Button", "font" );
      result.textColor = tv.getCssColor( "Button", "color" );
      result.spacing = tv.getCssDimension( "Button", "spacing" );
      result.padding = tv.getCssBoxDimensions( "Slider-UpButton", "padding" );
      result.backgroundColor = tv.getCssColor( "Slider-UpButton", "background-color" );
      result.icon = tv.getCssSizedImage( "Slider-UpButton-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssSizedImage( "Slider-UpButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "Slider-UpButton", "background-image" );
      }
      result.backgroundGradient = tv.getCssGradient( "Slider-UpButton", "background-image" );
      result.border = tv.getCssBorder( "Slider-UpButton", "border" );
      if( states[ "rwt_HORIZONTAL" ] ) {
        result.width = 16;
      } else {
        result.height = 16;
      }
      result.cursor = tv.getCssCursor( "Slider-UpButton", "cursor" );
      return result;
    }
  }

// END TEMPLATE //
};
