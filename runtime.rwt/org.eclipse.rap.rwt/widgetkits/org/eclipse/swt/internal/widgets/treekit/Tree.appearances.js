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

  "tree" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundColor : tv.getCssColor( "Tree", "background-color" ),
        textColor : tv.getCssColor( "Tree", "color" ),
        font : tv.getCssFont( "*", "font" ),
        border : tv.getCssBorder( "Tree", "border" )
      };
    }
  },

  "tree-row" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.itemBackground = tv.getCssColor( "TreeItem", "background-color" );
      result.itemBackgroundImage = tv.getCssImage( "TreeItem", "background-image" );
      result.itemBackgroundGradient = tv.getCssGradient( "TreeItem", "background-image" );
      result.itemForeground = tv.getCssColor( "TreeItem", "color" );
      result.overlayBackground = tv.getCssColor( "Tree-RowOverlay", "background-color" );
      result.overlayBackgroundImage = tv.getCssImage( "Tree-RowOverlay", "background-image" );
      result.overlayBackgroundGradient = tv.getCssGradient( "Tree-RowOverlay", "background-image" );
      result.overlayForeground = tv.getCssColor( "Tree-RowOverlay", "color" );
      result.textDecoration = tv.getCssIdentifier( "TreeItem", "text-decoration" );
      result.textShadow = tv.getCssShadow( "TreeItem", "text-shadow" );
      return result;
    }
  },

  "tree-row-check-box" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundImage : tv.getCssImage( "Tree-Checkbox", "background-image" )
      };
    }
  },

  "tree-row-indent" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundImage : tv.getCssImage( "Tree-Indent", "background-image" )
      };
    }
  },

  "tree-column" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.cursor = "default";
      result.spacing = 2;
      result.textColor = tv.getCssColor( "TreeColumn", "color" );
      result.font = tv.getCssFont( "TreeColumn", "font" );
      result.backgroundColor = tv.getCssColor( "TreeColumn", "background-color" );
      result.backgroundImage = tv.getCssImage( "TreeColumn", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "TreeColumn", "background-image" );
      result.opacity = states.moving ? 0.6 : 1.0;
      result.padding = tv.getCssBoxDimensions( "TreeColumn", "padding" );
      var borderColors = [ null, null, null, null ];
      var borderWidths = [ 0, 0, 0, 0 ];
      var borderStyles = [ "solid", "solid", "solid", "solid" ];
      if( !states.dummy ) {
        var verticalState = { "vertical" : true };
        var tvGrid = new org.eclipse.swt.theme.ThemeValues( verticalState );
        var gridColor = tvGrid.getCssColor( "Tree-GridLine", "color" );
        gridColor = gridColor == "undefined" ? "transparent" : gridColor;
        borderColors[ 1 ] = gridColor;
        borderWidths[ 1 ] = 1;
      }
      var borderBottom = tv.getCssBorder( "TreeColumn", "border-bottom" );
      borderWidths[ 2 ] = borderBottom.getWidthBottom();
      borderStyles[ 2 ] = borderBottom.getStyleBottom();
      borderColors[ 2 ] = borderBottom.getColorBottom();
      result.border = new org.eclipse.rwt.Border( borderWidths, borderStyles, borderColors );
      result.textShadow = tv.getCssShadow( "TreeColumn", "text-shadow" );
      return result;
    }
  },

  "tree-column-sort-indicator" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.source = tv.getCssImage( "TreeColumn-SortIndicator", "background-image" );
      return result;
    }
  }

// END TEMPLATE //
};
