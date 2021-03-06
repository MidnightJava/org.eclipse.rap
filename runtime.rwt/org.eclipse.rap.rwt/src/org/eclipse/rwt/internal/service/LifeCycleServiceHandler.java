/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.SingletonManager;
import org.eclipse.rwt.internal.application.ApplicationContext;
import org.eclipse.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.lifecycle.LifeCycle;
import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rwt.internal.lifecycle.RWTRequestVersionControl;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.theme.JsonValue;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISessionStore;


public class LifeCycleServiceHandler implements IServiceHandler {
  private static final String PROP_ERROR = "error";
  private static final String PROP_MESSAGE = "message";
  private static final String SESSION_STARTED
    = LifeCycleServiceHandler.class.getName() + "#isSessionStarted";

  private final LifeCycleFactory lifeCycleFactory;
  private final StartupPage startupPage;

  public LifeCycleServiceHandler( LifeCycleFactory lifeCycleFactory, StartupPage startupPage ) {
    this.lifeCycleFactory = lifeCycleFactory;
    this.startupPage = startupPage;
  }

  public void service() throws IOException {
    // Do not use session store itself as a lock
    // see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=372946
    SessionStoreImpl sessionStore = ( SessionStoreImpl )ContextProvider.getSessionStore();
    synchronized( sessionStore.getRequestLock() ) {
      synchronizedService();
    }
  }

  void synchronizedService() throws IOException {
    if( HTTP.METHOD_GET.equals( ContextProvider.getRequest().getMethod() ) ) {
      handleGetRequest();
    } else {
      try {
        handlePostRequest();
      } finally {
        markSessionStarted();
      }
    }
  }

  private void handleGetRequest() throws IOException {
    Map<String, String[]> parameters = ContextProvider.getRequest().getParameterMap();
    RequestParameterBuffer.store( parameters );
    startupPage.send();
  }

  private void handlePostRequest() throws IOException {
    setJsonResponseHeaders();
    if( isSessionTimeout() ) {
      handleSessionTimeout();
    } else if( !isRequestCounterValid() ) {
      handleInvalidRequestCounter();
    } else {
      if( isSessionRestart() ) {
        reinitializeSessionStore();
        clearServiceStore();
      }
      RequestParameterBuffer.merge();
      runLifeCycle();
    }
    writeProtocolMessage();
  }

  private void runLifeCycle() throws IOException {
    LifeCycle lifeCycle = ( LifeCycle )lifeCycleFactory.getLifeCycle();
    lifeCycle.execute();
  }

  //////////////////
  // helping methods

  private static boolean isRequestCounterValid() {
    return hasInitializeParameter() || RWTRequestVersionControl.getInstance().isValid();
  }

  private static void handleInvalidRequestCounter() {
    int statusCode = HttpServletResponse.SC_PRECONDITION_FAILED;
    String errorType = "invalid request counter";
    String errorMessage = RWTMessages.getMessage( "RWT_MultipleInstancesErrorMessage" );
    renderError( statusCode, errorType, formatMessage( errorMessage ) );
  }

  private static void handleSessionTimeout() {
    int statusCode = HttpServletResponse.SC_FORBIDDEN;
    String errorType = "session timeout";
    String errorMessage = RWTMessages.getMessage( "RWT_SessionTimeoutErrorMessage" );
    renderError( statusCode, errorType, formatMessage( errorMessage ) );
  }

  private static String formatMessage( String message ) {
    Object[] arguments = new Object[]{ "<a {HREF_URL}>", "</a>" };
    return MessageFormat.format( message, arguments );
  }

  private static void renderError( int statusCode, String errorType, String errorMessage) {
    ContextProvider.getResponse().setStatus( statusCode );
    ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
    writer.appendMeta( PROP_ERROR, JsonValue.valueOf( errorType ) );
    writer.appendMeta( PROP_MESSAGE, JsonValue.valueOf( errorMessage ) );
  }

  private static void reinitializeSessionStore() {
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    Integer version = RWTRequestVersionControl.getInstance().getCurrentRequestId();
    Map<String, String[]> bufferedParameters = RequestParameterBuffer.getBufferedParameters();
    ApplicationContext applicationContext = ApplicationContextUtil.get( sessionStore );
    clearSessionStore();
    RWTRequestVersionControl.getInstance().setCurrentRequestId( version );
    if( bufferedParameters != null ) {
      RequestParameterBuffer.store( bufferedParameters );
    }
    ApplicationContextUtil.set( sessionStore, applicationContext );
    AbstractBranding branding = BrandingUtil.determineBranding();
    if( branding.getThemeId() != null ) {
      ThemeUtil.setCurrentThemeId( branding.getThemeId() );
    }
  }

  private static void clearSessionStore() {
    SessionStoreImpl sessionStore = ( SessionStoreImpl )ContextProvider.getSessionStore();
    // clear attributes of session store to enable new startup
    sessionStore.valueUnbound( null );
    // reinitialize session store state
    sessionStore.valueBound( null );
    // TODO [rh] ContextProvider#getSessionStore() also initializes a session (slightly different)
    //      merge both code passages
    SingletonManager.install( sessionStore );
  }

  private static void clearServiceStore() {
    ServiceStore serviceStore = ( ServiceStore )ContextProvider.getServiceStore();
    serviceStore.clear();
  }

  /*
   * Session restart: we're in the same HttpSession and start over (e.g. by pressing F5)
   */
  private static boolean isSessionRestart() {
    return isSessionStarted() && hasInitializeParameter();
  }

  private static boolean isSessionTimeout() {
    // Session is not initialized because we got a new HTTPSession
    return !isSessionStarted() && !hasInitializeParameter();
  }

  static void markSessionStarted() {
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    sessionStore.setAttribute( SESSION_STARTED, Boolean.TRUE );
  }

  private static boolean isSessionStarted() {
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    return Boolean.TRUE.equals( sessionStore.getAttribute( SESSION_STARTED ) );
  }

  private static boolean hasInitializeParameter() {
    HttpServletRequest request = ContextProvider.getRequest();
    String initializeParameter = request.getParameter( RequestParams.RWT_INITIALIZE );
    return "true".equals( initializeParameter );
  }

  private static void setJsonResponseHeaders() {
    ServletResponse response = ContextProvider.getResponse();
    response.setContentType( HTTP.CONTENT_TYPE_JSON );
    response.setCharacterEncoding( HTTP.CHARSET_UTF_8 );
  }

  private static void writeProtocolMessage() throws IOException {
    HttpServletResponse response = ContextProvider.getResponse();
    ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
    String message = protocolWriter.createMessage();
    response.getWriter().write( message );
  }
}
