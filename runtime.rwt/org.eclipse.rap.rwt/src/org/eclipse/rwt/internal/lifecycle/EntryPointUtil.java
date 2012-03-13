/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rwt.service.ISessionStore;


public class EntryPointUtil {

  private static final String ATTR_CURRENT_ENTRY_POINT
    = EntryPointUtil.class.getName() + "#currentEntryPoint";

  public static final String DEFAULT = "default";

  private EntryPointUtil() {
    // prevent instantiation
  }

  public static IEntryPoint getCurrentEntryPoint() {
    EntryPointRegistration registration = getCurrentEntryPointRegistration();
    return registration.getFactory().create();
  }

  public static Map<String, Object> getCurrentEntryPointProperties() {
    EntryPointRegistration registration = getCurrentEntryPointRegistration();
    return registration.getProperties();
  }

  private static EntryPointRegistration getCurrentEntryPointRegistration() {
    EntryPointRegistration registration = readCurrentEntryPointRegistration();
    if( registration == null ) {
      registration = determineCurrentEntryPoint();
      storeCurrentEntryPointRegistration( registration );
    }
    return registration;
  }

  private static EntryPointRegistration determineCurrentEntryPoint() {
    EntryPointRegistration result;
    result = findByStartupParameter();
    if( result == null ) {
      result = findByServletPath();
      if( result == null ) {
        result = findByBranding();
        if( result == null ) {
          result = getEntryPointByName( DEFAULT );
        }
      }
    }
    return result;
  }

  private static EntryPointRegistration findByStartupParameter() {
    EntryPointRegistration result = null;
    HttpServletRequest request = ContextProvider.getRequest();
    String name = request.getParameter( RequestParams.STARTUP );
    if( name != null && name.length() > 0 ) {
      result = getEntryPointByName( name );
    }
    return result;
  }

  private static EntryPointRegistration findByServletPath() {
    EntryPointRegistration result = null;
    HttpServletRequest request = ContextProvider.getRequest();
    String path = request.getServletPath();
    if( path != null && path.length() > 0 ) {
      EntryPointManager entryPointManager = RWTFactory.getEntryPointManager();
      result = entryPointManager.getRegistrationByPath( path );
    }
    return result;
  }

  private static EntryPointRegistration findByBranding() {
    EntryPointRegistration result = null;
    AbstractBranding branding = BrandingUtil.determineBranding();
    String name = branding.getDefaultEntryPoint();
    if( name != null && name.length() > 0 ) {
      result = getEntryPointByName( name );
    }
    return result;
  }

  private static EntryPointRegistration getEntryPointByName( String name ) {
    EntryPointManager entryPointManager = RWTFactory.getEntryPointManager();
    IEntryPointFactory factory = entryPointManager.getFactoryByName( name );
    if( factory == null ) {
      throw new IllegalArgumentException( "Entry point not found: " + name );
    }
    return new EntryPointRegistration( factory, null );
  }

  private static void storeCurrentEntryPointRegistration( EntryPointRegistration registration ) {
    ISessionStore session = ContextProvider.getSessionStore();
    session.setAttribute( ATTR_CURRENT_ENTRY_POINT, registration );
  }

  private static EntryPointRegistration readCurrentEntryPointRegistration() {
    ISessionStore session = ContextProvider.getSessionStore();
    return ( EntryPointRegistration )session.getAttribute( ATTR_CURRENT_ENTRY_POINT );
  }

}
