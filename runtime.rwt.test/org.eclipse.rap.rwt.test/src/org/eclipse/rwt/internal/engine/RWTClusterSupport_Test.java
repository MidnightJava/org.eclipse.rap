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
package org.eclipse.rwt.internal.engine;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.*;
import org.eclipse.rwt.internal.service.SessionStoreImpl;


public class RWTClusterSupport_Test extends TestCase {

  private static class TestFilterChain implements FilterChain {
    boolean doFilterWasCalled;
    public void doFilter( ServletRequest request, ServletResponse response )
      throws IOException, ServletException
    {
      doFilterWasCalled = true;
    }
  }

  private RWTClusterSupport rwtClusterSupport;
  private TestFilterChain chain;
  private TestRequest request;
  private TestResponse response;

  public void testWithNonExistingSession() throws Exception {
    request.setSession( null );

    rwtClusterSupport.doFilter( request, response, chain );

    assertTrue( chain.doFilterWasCalled );
  }

  public void testSessionStoreGetsAttached() throws Exception {
    HttpSession session = new TestSession();
    request.setSession( session );
    session.setAttribute( SessionStoreImpl.ATTR_SESSION_STORE, new SessionStoreImpl( session ) );

    rwtClusterSupport.doFilter( request, response, chain );

    SessionStoreImpl sessionStore = SessionStoreImpl.getInstanceFromSession( session );
    assertTrue( chain.doFilterWasCalled );
    assertSame( session, sessionStore.getHttpSession() );
  }

  public void testSessionIsMarkedAsChanged() throws Exception {
    final StringBuilder log = new StringBuilder();
    HttpSession session = new TestSession() {
      public void setAttribute( String name, Object value ) {
        super.setAttribute( name, value );
        if( log.length() > 0 ) {
          log.append( ", " );
        }
        log.append( name );
      }
    };
    request.setSession( session );
    session.setAttribute( "foo", "bar" );
    SessionStoreImpl.attachInstanceToSession( session, new SessionStoreImpl( session ) );
    log.setLength( 0 );

    rwtClusterSupport.doFilter( request, response, chain );

    assertEquals( SessionStoreImpl.ATTR_SESSION_STORE, log.toString() );
  }

  protected void setUp() throws Exception {
    request = new TestRequest();
    response = new TestResponse();
    chain = new TestFilterChain();
    rwtClusterSupport = new RWTClusterSupport();
  }
}
