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

  "combo" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "Combo", "border" );
      result.backgroundColor = tv.getCssColor( "Combo", "background-color" );
      result.backgroundGradient = tv.getCssGradient( "Combo", "background-image" );
      result.textColor = tv.getCssColor( "Combo", "color" );
      result.font = tv.getCssFont( "Combo", "font" );
      return result;
    }
  },

  "combo-list" : {
    include : "list",
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.border = tv.getCssBorder( "Combo-List", "border" );
      result.textColor = tv.getCssColor( "Combo", "color" );
      result.font = tv.getCssFont( "Combo", "font" );
      result.backgroundColor = tv.getCssColor( "Combo", "background-color" );
      result.shadow = tv.getCssShadow( "Combo-List", "box-shadow" );
      result.textShadow = tv.getCssShadow( "Combo", "text-shadow" );
      return result;
    }
  },

  "combo-field" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.font = tv.getCssFont( "Combo", "font" );
      // [if] Do not apply top/bottom paddings on the client
      var cssPadding = tv.getCssBoxDimensions( "Combo-Field", "padding" );
      result.paddingRight = cssPadding[ 1 ];
      result.paddingLeft = cssPadding[ 3 ];
      result.width = null;
      result.height = null;
      result.left = 0;
      result.right = tv.getCssDimension( "Combo-Button", "width" );
      result.top = 0;
      result.bottom = 0;
      result.textColor = tv.getCssColor( "Combo", "color" );
      result.textShadow = tv.getCssShadow( "Combo", "text-shadow" );
      return result;
    }
  },

  "combo-button" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      var border = tv.getCssBorder( "Combo-Button", "border" );
      var borderLeft = tv.getCssBorder( "Combo-Button", "border-left" );
      result.border = org.eclipse.rwt.Border.mergeBorders( border, null, null, null, borderLeft );
      result.width = tv.getCssDimension( "Combo-Button", "width" );
      result.height = null;
      result.top = 0;
      result.bottom = 0;
      result.right = 0;
      result.icon = tv.getCssImage( "Combo-Button-Icon", "background-image" );
      if( result.icon === org.eclipse.swt.theme.ThemeValues.NONE_IMAGE ) {
        result.icon = tv.getCssImage( "Combo-Button", "background-image" );
      } else {
        result.backgroundImage = tv.getCssImage( "Combo-Button", "background-image" );
      }
      result.backgroundGradient = tv.getCssGradient( "Combo-Button", "background-image" );
      // TODO [rst] rather use button.bgcolor?
      result.backgroundColor = tv.getCssColor( "Combo-Button", "background-color" );
      result.cursor = tv.getCssCursor( "Combo-Button", "cursor" );
      return result;
    }
  }

// END TEMPLATE //
};