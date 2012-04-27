/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.service.IServiceStore;
import org.eclipse.swt.widgets.Display;


public final class JSExecutor {

  private static final String JS_EXECUTOR = JSExecutor.class.getName() + "#instance";
  private static final String JSEXECUTOR_ID = "jsex";
  private static final String JSEXECUTOR_TYPE = "rwt.JSExecutor";
  private static final String PARAM_CONTENT = "content";
  private static final String METHOD_EXECUTE = "execute";

  public static void executeJS( String code ) {
    JSExecutorPhaseListener jsExecutor = getJSExecutor();
    if( jsExecutor == null ) {
      jsExecutor = new JSExecutorPhaseListener();
      RWTFactory.getLifeCycleFactory().getLifeCycle().addPhaseListener( jsExecutor );
      setJSExecutor( jsExecutor );
    }
    jsExecutor.append( code );
  }

  private JSExecutor() {
    ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
    protocolWriter.appendCreate( JSEXECUTOR_ID, JSEXECUTOR_TYPE );
  }

  private static void ensureInstance() {
    SessionSingletonBase.getInstance( JSExecutor.class );
  }

  private static JSExecutorPhaseListener getJSExecutor() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return ( JSExecutorPhaseListener )serviceStore.getAttribute( JS_EXECUTOR );
  }

  private static void setJSExecutor( JSExecutorPhaseListener jsExecutor ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( JS_EXECUTOR, jsExecutor );
  }

  private static class JSExecutorPhaseListener implements PhaseListener {
    private final StringBuilder code;
    private final Display display;

    JSExecutorPhaseListener() {
      display =  Display.getCurrent() ;
      code = new StringBuilder();
    }

    void append( String command ) {
      code.append( command );
    }

    public void beforePhase( PhaseEvent event ) {
      // do nothing
    }

    public void afterPhase( PhaseEvent event ) {
      if( display == LifeCycleUtil.getSessionDisplay() ) {
        ensureInstance();
        ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
        try {
          Map<String, Object> properties = new HashMap<String, Object>();
          properties.put( PARAM_CONTENT, code.toString().trim() );
          protocolWriter.appendCall( JSEXECUTOR_ID, METHOD_EXECUTE, properties );
        } finally {
          RWTFactory.getLifeCycleFactory().getLifeCycle().removePhaseListener( this );
        }
      }
    }

    public PhaseId getPhaseId() {
      return PhaseId.RENDER;
    }
  }
}
