/*******************************************************************************
 * Copyright (c) 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                          and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   1&1 Internet AG and others - original API and implementation
 *   EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define("qx.ui.pageview.AbstractButton", {

  type : "abstract",
  extend : qx.ui.basic.Atom,

  construct : function( vText, vIcon, vIconWidth, vIconHeight, vFlash ) {
    this.base( arguments, vText, vIcon, vIconWidth, vIconHeight, vFlash );
    this.initChecked();
    this.initTabIndex();
    this.addEventListener("mouseover", this._onmouseover);
    this.addEventListener("mouseout", this._onmouseout);
    this.addEventListener("mousedown", this._onmousedown);
    this.addEventListener("keydown", this._onkeydown);
    this.addEventListener("keypress", this._onkeypress);
  },

  properties : {

    tabIndex : {
      refine : true,
      init : 1
    },

    /** If this tab is the currently selected/active one */
    checked : {
      check :"Boolean",
      init : false,
      apply : "_applyChecked",
      event : "changeChecked"
    },

    /** The attached page of this tab */
    page : {
      check : "qx.ui.pageview.AbstractPage",
      apply : "_applyPage",
      nullable : true
    },

    /** The assigned qx.ui.selection.RadioManager which handles the switching between registered buttons */
    manager : {
      check  : "qx.ui.selection.RadioManager",
      nullable : true,
      apply : "_applyManager"
    },

    /**
     * The name of the radio group. All the radio elements in a group (registered by the same manager)
     *  have the same name (and could have a different value).
     */
    name : {
      check : "String",
      apply : "_applyName"
    }
  },

  members : {

    /**
     * TODOC
     *
     * @type member
     * @return {var} TODOC
     */
    getView : function() {
      var pa = this.getParent();
      return pa ? pa.getParent() : null;
    },

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyManager : function( value, old ) {
      if( old ) {
        old.remove( this );
      }
      if( value ) {
        value.add( this );
      }
    },

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     * @return {var} TODOC
     */
    _applyParent : function( value, old ) {
      this.base( arguments, value, old );
      if ( old ) {
        old.getManager().remove( this );
      }
      if( value ) {
        value.getManager().add( this );
      }
    },

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyPage : function( value, old ) {
      if( old ) {
        old.setButton( null );
      }
      if( value ) {
        value.setButton( this );
        if( this.getChecked() ) {
          value.show();
        } else {
          value.hide();
        }
      }
    },

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyChecked : function( value, old )
    {
      if( this._hasParent ) {
        var vManager = this.getManager();
        if( vManager ) {
          vManager.handleItemChecked(this, value);
        }
      }
      if( value ) {
        this.addState( "checked" );
      } else {
        this.removeState( "checked" );
      }
      var vPage = this.getPage();
      if( vPage ) {
        if( this.getChecked() ) {
          vPage.show();
        } else {
          vPage.hide();
        }
      }
    },

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyName : function( value, old ) {
      if( this.getManager() ) {
        this.getManager().setName(value);
      }
    },

    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {void}
     */
    _onmousedown : function( e ) {
      this.setChecked( true );
    },

    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {void}
     */
    _onmouseover : function( e ) {
      this.addState( "over" );
    },

    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {void}
     */
    _onmouseout : function( e ) {
      this.removeState( "over" );
    },

    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {void}
     */
    _onkeydown : function( e ) {},

    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {void}
     */
    _onkeypress : function( e ) {}

  }

});
