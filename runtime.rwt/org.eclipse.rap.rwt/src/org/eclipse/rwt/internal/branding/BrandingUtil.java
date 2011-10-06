/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.branding;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.branding.Header;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.util.URLHelper;


public final class BrandingUtil {

  private static final String ATTR_BRANDING_ID = BrandingUtil.class.getName() + "#brandingId";

  public static void replacePlaceholder( StartupPageTemplateHolder template, 
                                         StartupPageTemplateHolder.Variable variable, 
                                         String replacement ) 
  {
    String safeReplacement = replacement == null ? "" : replacement;
    template.replace( variable, safeReplacement );
  }

  public static String headerMarkup( AbstractBranding branding ) {
    Header[] headers = branding.getHeaders();
    StringBuffer buffer = new StringBuffer();
    appendFavIconMarkup( buffer, branding );
    if( headers != null ) {
      for( int i = 0; i < headers.length; i++ ) {
        Header header = headers[ i ];
        appendHeaderMarkup( buffer, header );
        buffer.append( "\n" );
      }
    }
    return buffer.toString();
  }
  
  public static AbstractBranding determineBranding() {
    HttpServletRequest request = ContextProvider.getRequest();
    String servletName = URLHelper.getServletName();
    String entryPoint = request.getParameter( RequestParams.STARTUP );
    AbstractBranding result = RWTFactory.getBrandingManager().find( servletName, entryPoint );
    RWT.getSessionStore().setAttribute( ATTR_BRANDING_ID, result.getId() );
    return result;
  }
  
  /**
   * Return the id of the current branding. This is only available after 
   * {@link #determineBranding()} has been called.
   * @return the id of the current branding or <code>null</code>.
   */
  public static String getCurrentBrandingId() {
    return ( String )RWT.getSessionStore().getAttribute( ATTR_BRANDING_ID );
  }
  
  //////////////////
  // Helping methods
  
  private static void appendFavIconMarkup( StringBuffer buffer, AbstractBranding branding ) {
    String favIcon = branding.getFavIcon();
    if( favIcon != null && !"".equals( favIcon ) ) {
      String[] names = new String[] { 
        "rel", 
        "type", 
        "href" 
      };
      String favIconUrl = RWT.getResourceManager().getLocation( favIcon );
      String[] values = new String[] { 
        "shortcut icon", 
        "image/x-icon", 
        favIconUrl
      };
      Header header = new Header( "link", names, values );
      appendHeaderMarkup( buffer, header );
      buffer.append( "\n" );
    }
  }

  private static String appendHeaderMarkup( StringBuffer buffer, Header header ) {
    buffer.append( "<" );
    buffer.append( header.getTagName() );
    buffer.append( " " );
    String[] names = header.getNames();
    String[] values = header.getValues();
    for( int i = 0; i < names.length; i++ ) {
      String name = names[ i ];
      String value = values[ i ];
      if( name != null && value != null ) {
        buffer.append( name );
        buffer.append( "=\"" );
        buffer.append( value );
        buffer.append( "\" " );
      }
    }
    buffer.append( "/>" );
    return buffer.toString();
  }

  private BrandingUtil() {
    // prevent instantiation
  }
}
