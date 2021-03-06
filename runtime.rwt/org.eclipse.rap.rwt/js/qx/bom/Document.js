/*******************************************************************************
 *  Copyright: 2004, 2010 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 * 
 *  This class contains code based on the following work:
 *
 *  * Yahoo! UI Library
 *      http://developer.yahoo.com/yui
 *      Version 2.2.0
 *
 *    Copyright:
 *      (c) 2007, Yahoo! Inc.
 *
 *    License:
 *      BSD: http://developer.yahoo.com/yui/license.txt
 * 
 ******************************************************************************/

/**
 * Includes library functions to work with the current document.
 */
qx.Class.define("qx.bom.Document",
{
  statics :
  {
    /**
     * Whether the document is in quirks mode (e.g. non XHTML, HTML4 Strict or missing doctype)
     *
     * @type static
     * @param win {Window?window} The window to query
     * @return {Boolean} true when containing document is in quirks mode
     */
    isQuirksMode : function(win) {
      return (win||window).document.compatMode !== "CSS1Compat";
    },


    /**
     * Whether the document is in quirks mode (e.g. non XHTML, HTML4 Strict or missing doctype)
     *
     * @type static
     * @param win {Window?window} The window to query
     * @return {Boolean} true when containing document is in quirks mode
     */
    isStandardMode : function(win) {
      return (win||window).document.compatMode === "CSS1Compat";
    },


    /**
     * Returns the width of the document.
     *
     * Internet Explorer in standard mode stores the properitary <code>scrollWidth</code> property
     * on the <code>documentElement</code>, but in quirks mode on the body element. All
     * other known browsers simply store the correct value on the <code>documentElement</code>.
     *
     * If the viewport is wider than the document the viewport width is returned.
     *
     * As the html element has no visual appearance it also can not scroll. This
     * means that we must use the body <code>scrollWidth</code> in all non mshtml clients.
     *
     * Verified to correctly work with:
     *
     * * Mozilla Firefox 2.0.0.4
     * * Opera 9.2.1
     * * Safari 3.0 beta (3.0.2)
     * * Internet Explorer 7.0
     *
     * @type static
     * @param win {Window?window} The window to query
     * @return {Integer} The width of the actual document (which includes the body and its margin).
     */
    getWidth : function(win)
    {
      var doc = (win||window).document;
      var view = qx.bom.Viewport.getWidth(win);
      var scroll = doc.compatMode === "CSS1Compat" ? doc.documentElement.scrollWidth : doc.body.scrollWidth;
      return Math.max(scroll, view);
    },


    /**
     * Returns the height of the document.
     *
     * Internet Explorer in standard mode stores the properitary <code>scrollHeight</code> property
     * on the <code>documentElement</code>, but in quirks mode on the body element. All
     * other known browsers simply store the correct value on the <code>documentElement</code>.
     *
     * If the viewport is higher than the document the viewport height is returned.
     *
     * As the html element has no visual appearance it also can not scroll. This
     * means that we must use the body <code>scrollHeight</code> in all non mshtml clients.
     *
     * Verified to correctly work with:
     *
     * * Mozilla Firefox 2.0.0.4
     * * Opera 9.2.1
     * * Safari 3.0 beta (3.0.2)
     * * Internet Explorer 7.0
     *
     * @type static
     * @param win {Window?window} The window to query
     * @return {Integer} The height of the actual document (which includes the body and its margin).
     */
    getHeight : function(win)
    {
      var doc = (win||window).document;
      var view = qx.bom.Viewport.getHeight(win);
      var scroll = doc.compatMode === "CSS1Compat" ? doc.documentElement.scrollHeight : doc.body.scrollHeight;
      return Math.max(scroll, view);
    }
  }
});
