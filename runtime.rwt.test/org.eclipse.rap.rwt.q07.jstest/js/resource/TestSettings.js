/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

( function() {

  var getURLParam = function( name ) {
    var result = null;
    var href = window.location.href;
    var hashes = href.slice( href.indexOf( "?" ) + 1).split( "&" );
    for( var i = 0; i < hashes.length; i++ ) {
      var hash = hashes[ i ].split( "=" );
      if( hash[ 0 ] === name ) {
        result = hash[ 1 ];
      }
    }
    return result;
  };

  qxsettings = {};
  qxsettings[ "qx.application" ] = "org.eclipse.rwt.test.Application";
  qxsettings[ "qx.minLogLevel" ] = 200;
  qxsettings[ "qx.logAppender" ] = "qx.log.appender.Native";
  qxsettings[ "qx.version" ] = "0.7.4 ";
  qxsettings[ "qx.isSource"] = true;
  qxsettings[ "qx.resourceUri" ] = "./rwt-resources/";
  qxsettings[ "qx.theme" ] = "org.eclipse.swt.theme.Default";
  qxvariants = {};
  qxvariants[ "qx.compatibility" ] = "off";
  qxvariants[ "qx.aspects" ] = "off";
  qxvariants[ "qx.debug" ] = getURLParam( "debug" ) === "off" ? "off" : "on";

} )();
