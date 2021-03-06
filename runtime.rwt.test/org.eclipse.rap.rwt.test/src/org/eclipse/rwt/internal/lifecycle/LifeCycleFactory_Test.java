/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.lifecycle.PhaseListener;


public class LifeCycleFactory_Test extends TestCase {
  private LifeCycleFactory lifeCycleFactory;
  private PhaseListenerRegistry phaseListenerRegistry;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    phaseListenerRegistry = new PhaseListenerRegistry();
    lifeCycleFactory = new LifeCycleFactory( phaseListenerRegistry );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testActivateDeactivateCycle() {
    lifeCycleFactory.configure( TestLifeCycle.class );
    ILifeCycle beforeActivate = lifeCycleFactory.getLifeCycle();
    lifeCycleFactory.activate();
    ILifeCycle afterActivate = lifeCycleFactory.getLifeCycle();
    ILifeCycle secondCall = lifeCycleFactory.getLifeCycle();
    lifeCycleFactory.deactivate();
    ILifeCycle afterDeactivate = lifeCycleFactory.getLifeCycle();

    assertNull( beforeActivate );
    assertTrue( afterActivate instanceof TestLifeCycle );
    assertSame( afterActivate, secondCall );
    assertNull( afterDeactivate );
  }

  public void testActivateAfterDeactivate() {
    lifeCycleFactory.configure( TestLifeCycle.class );
    lifeCycleFactory.activate();

    lifeCycleFactory.deactivate();
    lifeCycleFactory.activate();

    Class<?> lifeCycleClass = lifeCycleFactory.getLifeCycle().getClass();
    assertSame( SimpleLifeCycle.class, lifeCycleClass );
  }

  public void testConfigure() {
    lifeCycleFactory.configure( TestLifeCycle.class );
    lifeCycleFactory.activate();

    Class<?> lifeCycleClass = lifeCycleFactory.getLifeCycle().getClass();

    assertSame( TestLifeCycle.class, lifeCycleClass );
  }

  public void testDefaultLifeCycle() {
    lifeCycleFactory.activate();

    Class<?> lifeCycleClass = lifeCycleFactory.getLifeCycle().getClass();

    assertSame( SimpleLifeCycle.class, lifeCycleClass );
  }

  public void testGetLifeCycleWithRegisteredPhaseListeners() {
    PhaseListener phaseListener = mock( PhaseListener.class );
    phaseListenerRegistry.add( phaseListener );
    lifeCycleFactory.configure( TestLifeCycle.class );
    lifeCycleFactory.activate();

    TestLifeCycle lifeCycle = ( TestLifeCycle )lifeCycleFactory.getLifeCycle();

    assertSame( phaseListener, lifeCycle.addedPhaseListener );
  }

  private static class TestLifeCycle extends LifeCycle {
    PhaseListener addedPhaseListener;
    @Override
    public void addPhaseListener( PhaseListener listener ) {
      addedPhaseListener = listener;
    }
    @Override
    public void removePhaseListener( PhaseListener listener ) {
    }
    @Override
    public void execute() {
    }
    @Override
    public void requestThreadExec( Runnable runnable ) {
    }
    @Override
    public void sleep() {
    }
  }

}
