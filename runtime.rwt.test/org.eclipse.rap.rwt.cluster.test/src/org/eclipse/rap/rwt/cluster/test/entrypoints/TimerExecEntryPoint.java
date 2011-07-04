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
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import java.io.Serializable;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;


public class TimerExecEntryPoint implements IEntryPoint {
  public static final int TIMER_DELAY = 2000;
  
  private static final String ATTRIBUTE_NAME = "wasInvoked";
  private static final Boolean ATTRIBUTE_VALUE = Boolean.TRUE;
  
  public static boolean wasRunnableExecuted( ISessionStore sessionStore ) {
    return ATTRIBUTE_VALUE.equals( sessionStore.getAttribute( ATTRIBUTE_NAME ) ); 
  }

  public int createUI() {
    Display display = new Display();
    display.timerExec( TIMER_DELAY, new TimerExecRunnable() );
    return 0;
  }

  private static class TimerExecRunnable implements Runnable, Serializable {
    private static final long serialVersionUID = 1L;

    public void run() {
      RWT.getSessionStore().setAttribute( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );
    }
  }
}
