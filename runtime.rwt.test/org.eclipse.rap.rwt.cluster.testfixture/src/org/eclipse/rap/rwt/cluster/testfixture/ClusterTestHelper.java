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
package org.eclipse.rap.rwt.cluster.testfixture;

import java.lang.reflect.Field;

import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.rwt.internal.service.SessionStoreImpl;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;


@SuppressWarnings("restriction")
public class ClusterTestHelper {

  public static void enableUITests( boolean enable ) {
    try {
      Field field = UITestUtil.class.getDeclaredField( "enabled" );
      field.setAccessible( true );
      field.set( null, Boolean.valueOf( enable ) );
    } catch( Exception e ) {
      throw new RuntimeException( "Failed to change enablement of UI Tests", e );
    } 
  }
  
  public static HttpSession getFirstSession( IServletEngine servletEngine ) {
    return servletEngine.getSessions()[ 0 ];
  }
  
  public static ISessionStore getFirstSessionStore( IServletEngine servletEngine ) {
    return getSessionStore( getFirstSession( servletEngine ) );
  }
  
  public static ISessionStore getSessionStore( HttpSession httpSession ) {
    return ( ISessionStore )httpSession.getAttribute( SessionStoreImpl.ATTR_SESSION_STORE );
  }

  public static Display getSessionDisplay( HttpSession httpSession ) {
    return LifeCycleUtil.getSessionDisplay( getSessionStore( httpSession ) );
  }

  private ClusterTestHelper() {
    // prevent instantiation
  }
}
