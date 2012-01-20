/*******************************************************************************
 *  Copyright: 2007, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
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


qx.Class.define("qx.util.manager.Value",
{
  type : "abstract",
  extend : qx.core.Target,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments);

    // Stores the objects
    this._registry = {};

    // Create empty dynamic map
    this._dynamic = {};

    this._connectedObjects = {};
  },





  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * Disconnect all connections to the given object.
     *
     * @type member
     * @param obj {qx.core.Object} The class, which should be disconnected.
     */
    disconnect : function(obj)
    {
      // If value is disposed, it's already disconnected
      if (this.isDisposed()) {
        return;
      }

      // Otherwise disconnect from this value
      var objectHash = obj.toHashCode();
      var connections = this._connectedObjects;
      var reg = this._registry;

      // Disconnect all keys
      var lKeys = connections[objectHash];
      while (lKeys.length) {
        delete reg[lKeys.pop()];
      }

      // Forget about object
      delete connections[objectHash];
    },


    /**
     * Processing a value and handle callback execution on updates.
     *
     * @type member
     * @param callback {Function} The callback function which handles the
     *   apply of the resulting dynamically resolved value.
     * @param obj {Object} The context, the callback will be caled with.
     * @param value {var} Any acceptable value, but no booleans and no undefined
     * @return {void}
     */
    connect : function(callback, obj, value)
    {
      // Store references for dynamic values
      var objectHash = obj.toHashCode();
      var key = "v" + objectHash + "$" + qx.core.Object.toHashCode(callback);
      var reg = this._registry;

      // Preprocess value (if function is defined)
      if (value !== null && this._preprocess) {
        value = this._preprocess(value);
      }

      // Callback handling
      if (this.isDynamic(value))
      {
        // Store reference for themed values
        reg[key] =
        {
          callback : callback,
          object   : obj,
          value    : value
        };

        // remember, which objects have connections
        if (!this._connectedObjects[objectHash]) {
          this._connectedObjects[objectHash] = [];
        }
        this._connectedObjects[objectHash].push(key);
        obj.hasConnectionTo(this);

      }
      else if (reg[key])
      {
        // In all other cases try to remove previously created references
        delete reg[key];
      }

      // Finally executing given callback
      callback.call(obj, this.resolveDynamic(value) || value);
    },


    /**
     * Returns the dynamically interpreted result for the incoming value
     *
     * @type member
     * @param value {String} dynamically interpreted idenfier
     * @return {var} return the (translated) result of the incoming value
     */
    resolveDynamic : function(value) {
      return this._dynamic[value];
    },


    /**
     * Whether a value is interpreted dynamically
     *
     * @type member
     * @param value {String} dynamically interpreted idenfier
     * @return {Boolean} returns true if the value is interpreted dynamically
     */
    isDynamic : function(value) {
      return this._dynamic[value] !== undefined;
    },


    /**
     * Update all registered objects regarding the value switch
     *
     * @type member
     */
    _updateObjects : function()
    {
      var reg = this._registry;
      var entry;

      for (var key in reg)
      {
        entry = reg[key];
        entry.callback.call(entry.object, this.resolveDynamic(entry.value));
      }
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeFields("_registry", "_dynamic", "_connectedObjects");
  }
});
