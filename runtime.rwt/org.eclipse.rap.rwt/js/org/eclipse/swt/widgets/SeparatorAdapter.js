/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Separator", {

  factory : function( properties ) {
    var result = new org.eclipse.swt.widgets.Separator();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    result.setLineOrientation( styleMap.VERTICAL ? "vertical" : "horizontal" );
    var lineStyle = "rwt_SHADOW_NONE";
    if( styleMap.SHADOW_IN ) {
      lineStyle = "rwt_SHADOW_IN";
    } else if( styleMap.SHADOW_OUT ) {
      lineStyle = "rwt_SHADOW_OUT";
    }
    result.setLineStyle( lineStyle );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {} ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );