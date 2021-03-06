/*******************************************************************************
 *  Copyright: 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define("org.eclipse.rwt.widgets.ListItem", {

  extend : org.eclipse.rwt.widgets.MultiCellWidget,

  construct : function() {
    this.base( arguments, [ "label" ] );
    this.initMinWidth();
    this.setHorizontalChildrenAlign( "left" );
  },

  properties : {

    appearance : {
      refine : true,
      init : "list-item"
    },

    minWidth : {
      refine : true,
      init : "auto"
    },

    width : {
      refine : true,
      init : null
    },

    allowStretchX : {
      refine : true,
      init : true
    }

  },

  members : {

    setLabel : function( value ) {
      this.setCellContent( 0, value );
    },

    getLabel : function( value ) {
      return this.getCellContent( 0 );
    },
    
    matchesString : function( value ) {
      var content;
      var el = this.getCellNode( 0 );
      if( el ) {
        content = el.innerText || el.textContent;
      } else {
        content = this.getLabel();
      }
      var input = ( typeof value === "string" ) ? value.toLowerCase() : "";
      content = ( typeof content === "string" ) ? content.toLowerCase() : "";
      return input !== "" && content.indexOf( input ) === 0;
    }
  
  }
} );
