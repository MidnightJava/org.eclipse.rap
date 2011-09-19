/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class RenderDispose_Test extends TestCase {

  public void testDisposeNotYetInitialized() {
    // set up the test widget hierarchy
    Display display = new Display();
    final Composite shell = new Shell( display , SWT.NONE );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    // first rendering: html document that contains the javaScript 'application'
    Fixture.executeLifeCycleFromServerThread( );
    // second rendering: initial markup that constructs the above created
    // widget hierarchy (display, shell and button)
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.executeLifeCycleFromServerThread( );
    // create and dispose of the button
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    ILifeCycle lifeCycle = RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;

      public void beforePhase( final PhaseEvent event ) {
        Button button = new Button( shell, SWT.PUSH );
        button.dispose();
      }

      public void afterPhase( final PhaseEvent event ) {
      }

      public PhaseId getPhaseId() {
        return PhaseId.RENDER;
      }
    } );
    Fixture.executeLifeCycleFromServerThread( );
    Message message = Fixture.getProtocolMessage();
    assertEquals( 1, message.getRequestCounter() );
    assertEquals( 0, message.getOperationCount() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
