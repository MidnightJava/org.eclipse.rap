/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
appearances = {
// BEGIN TEMPLATE //

  "scrollbar" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        border : tv.getCssBorder( "ScrollBar", "border" ),
        backgroundColor : tv.getCssColor( "ScrollBar", "background-color" ),
        backgroundImage : tv.getCssImage( "ScrollBar", "background-image" ),
        backgroundGradient : tv.getCssGradient( "ScrollBar", "background-image" )
      };
      var width = tv.getCssDimension( "ScrollBar", "width" );
      if( states[ "rwt_HORIZONTAL" ] ) {
        result.height = width;
      } else {
        result.width = width;
      }
      return result;
    }
  },

  "scrollbar-thumb" : {
    include : "atom",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.backgroundColor = tv.getCssColor( "ScrollBar-Thumb", "background-color" );
      result.border = tv.getCssBorder( "ScrollBar-Thumb", "border" );
      result.backgroundImage = tv.getCssImage( "ScrollBar-Thumb", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "ScrollBar-Thumb", "background-image" );
      result.icon = tv.getCssSizedImage( "ScrollBar-Thumb-Icon", "background-image" );
      return result;
    }
  },

  "scrollbar-min-button" : {
    include : "atom",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.spacing = tv.getCssDimension( "Button", "spacing" );
      result.padding = tv.getCssBoxDimensions( "Button", "padding" );
      result.backgroundColor = tv.getCssColor( "ScrollBar-DownButton", "background-color" );
      result.icon = tv.getCssSizedImage( "ScrollBar-DownButton-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssSizedImage( "ScrollBar-DownButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "ScrollBar-DownButton", "background-image" );
      }
      result.backgroundGradient = tv.getCssGradient( "ScrollBar-DownButton", "background-image" );
      result.border = tv.getCssBorder( "ScrollBar-DownButton", "border" );
      var width = tv.getCssDimension( "ScrollBar", "width" );
      if( states[ "rwt_HORIZONTAL" ] ){
        result.width = width;
      } else {
        result.height = width;
      }
      result.cursor = tv.getCssCursor( "ScrollBar-DownButton", "cursor" );
      return result;
    }
  },

  "scrollbar-max-button" : {
    include : "atom",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.font = tv.getCssFont( "Button", "font" );
      result.textColor = tv.getCssColor( "Button", "color" );
      result.spacing = tv.getCssDimension( "Button", "spacing" );
      result.padding = tv.getCssBoxDimensions( "Button", "padding" );
      result.backgroundColor = tv.getCssColor( "ScrollBar-UpButton", "background-color" );
      result.icon = tv.getCssSizedImage( "ScrollBar-UpButton-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssSizedImage( "ScrollBar-UpButton", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "ScrollBar-UpButton", "background-image" );
      }
      result.backgroundGradient = tv.getCssGradient( "ScrollBar-UpButton", "background-image" );
      result.border = tv.getCssBorder( "ScrollBar-UpButton", "border" );
      var width = tv.getCssDimension( "ScrollBar", "width" );
      if( states[ "rwt_HORIZONTAL" ] ) {
        result.width = width;
      } else {
        result.height = width;
      }
      result.cursor = tv.getCssCursor( "ScrollBar-UpButton", "cursor" );
      return result;
    }
  }

// END TEMPLATE //
};