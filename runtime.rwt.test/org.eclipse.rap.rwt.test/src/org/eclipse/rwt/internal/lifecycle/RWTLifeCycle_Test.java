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
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.IPhase.IInterruptible;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.internal.service.ServiceStore;
import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.ILifeCycleAdapter;
import org.eclipse.rwt.lifecycle.PhaseEvent;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rwt.service.IServiceStore;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.rwt.service.SessionStoreEvent;
import org.eclipse.rwt.service.SessionStoreListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class RWTLifeCycle_Test extends TestCase {

  private static final String ERR_MSG = "TEST_ERROR";
  private static final String MY_ENTRY_POINT = "myEntryPoint";
  private static final String BEFORE = "before ";
  private static final String AFTER = "after ";
  private static final String DISPLAY_CREATED = "display created";
  private static final String EXCEPTION_IN_RENDER = "Exception in render";

  private static StringBuilder log = new StringBuilder();

  @Override
  protected void setUp() throws Exception {
    log.setLength( 0 );
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testNoEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    try {
      lifeCycle.execute();
      fail( "Executing lifecycle without entry point must throw exception" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDefaultEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT,
                                                TestEntryPointWithLog.class );

    lifeCycle.execute();

    assertEquals( DISPLAY_CREATED, log.toString() );
  }

  public void testParamOfExistingEntryPoint() throws IOException {
    Fixture.fakeRequestParam( RequestParams.STARTUP, MY_ENTRY_POINT );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    RWTFactory.getEntryPointManager().register( MY_ENTRY_POINT, TestEntryPointWithLog.class );

    lifeCycle.execute();

    assertEquals( DISPLAY_CREATED, log.toString() );
  }

  public void testParamOfNonExistingEntryPoint() throws IOException {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    Fixture.fakeRequestParam( RequestParams.STARTUP, "notRegistered" );
    try {
      lifeCycle.execute();
      fail( "Executing lifecycle with unknown entry point must fail." );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testPhases() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, TestPhasesEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    PhaseListener listener = new PhaseListener() {
      private static final long serialVersionUID = 1L;

      public void afterPhase( PhaseEvent event ) {
        log.append( AFTER + event.getPhaseId() + "|" );
      }

      public void beforePhase( PhaseEvent event ) {
        log.append( BEFORE + event.getPhaseId() + "|" );
      }

      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    };
    lifeCycle.addPhaseListener( listener );
    lifeCycle.execute();
    String expected =   BEFORE
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + AFTER
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + BEFORE
                      + PhaseId.RENDER
                      + "|"
                      + AFTER
                      + PhaseId.RENDER
                      + "|";
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
    lifeCycle.execute();
    expected =   BEFORE
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + AFTER
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + BEFORE
               + PhaseId.READ_DATA
               + "|"
               + AFTER
               + PhaseId.READ_DATA
               + "|"
               + BEFORE
               + PhaseId.PROCESS_ACTION
               + "|"
               + AFTER
               + PhaseId.PROCESS_ACTION
               + "|"
               + BEFORE
               + PhaseId.RENDER
               + "|"
               + AFTER
               + PhaseId.RENDER
               + "|";
    assertEquals( expected, log.toString() );
    lifeCycle.removePhaseListener( listener );
    log.setLength( 0 );
    lifeCycle.execute();
    assertEquals( "", log.toString() );
    log.setLength( 0 );
    lifeCycle.addPhaseListener( new PhaseListener() {

      private static final long serialVersionUID = 1L;

      public void afterPhase( PhaseEvent event ) {
        log.append( AFTER + event.getPhaseId() + "|" );
      }

      public void beforePhase( PhaseEvent event ) {
        log.append( BEFORE + event.getPhaseId() + "|" );
      }

      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    } );
    lifeCycle.execute();
    expected =   BEFORE
               + PhaseId.PREPARE_UI_ROOT
               + "|"
               + AFTER
               + PhaseId.PREPARE_UI_ROOT
               + "|";
    assertEquals( expected, log.toString() );
  }

  public void testErrorInLifeCycle() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT,
                                                TestErrorInLifeCycleEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    LifeCycleUtil.setSessionDisplay( null );
    try {
      lifeCycle.execute();
      fail();
    } catch( RuntimeException e ) {
      String msg = TestErrorInLifeCycleEntryPoint.class.getName();
      assertEquals( msg, e.getMessage() );
      assertTrue( RWTLifeCycle.getUIThreadHolder().getThread().isAlive() );
    }
  }

  public void testExceptionInPhaseListener() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, TestEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new ExceptionListenerTest() );
    lifeCycle.addPhaseListener( new ExceptionListenerTest() );
    lifeCycle.execute();
    String expected = BEFORE
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + BEFORE
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + AFTER
                      + PhaseId.PREPARE_UI_ROOT
                      + "|"
                      + AFTER
                      + PhaseId.PREPARE_UI_ROOT
                      + "|";
    assertEquals( expected, log.toString() );
  }

  public void testRender() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, TestEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );

    lifeCycle.execute();

    Message message = Fixture.getProtocolMessage();
    assertTrue( message.getOperationCount() > 0 );
  }

  public void testPhaseListenerRegistration() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, TestEntryPoint.class );
    final PhaseListener[] callbackHandler = new PhaseListener[ 1 ];
    PhaseListener listener = new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( PhaseEvent event ) {
        callbackHandler[ 0 ] = this;
      }
      public void afterPhase( PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    };
    RWTFactory.getPhaseListenerRegistry().add( listener );
    RWTFactory.getLifeCycleFactory().activate();
    // Run lifecycle in session one
    RWTLifeCycle lifeCycle1 = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle1.execute();
    assertSame( listener, callbackHandler[ 0 ] );
    // Simulate new session and run lifecycle
    newSession();
    Fixture.fakeResponseWriter();
    callbackHandler[ 0 ] = null;
    RWTLifeCycle lifeCycle2 = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle2.execute();
    assertSame( listener, callbackHandler[ 0 ] );
  }

  public void testContinueLifeCycle() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void afterPhase( PhaseEvent event ) {
        log.append( "after" + event.getPhaseId() );
      }
      public void beforePhase( PhaseEvent event ) {
        log.append( "before" + event.getPhaseId() );
      }
      public PhaseId getPhaseId() {
        return PhaseId.ANY;
      }
    } );

    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          fail( "Interruptible phase should never get executed." );
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      },
      new IPhase() {
        public PhaseId execute(Display display) throws IOException {
          log.append( "execute" + getPhaseId() );
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.RENDER;
        }
      }
    } );
    lifeCycle.continueLifeCycle();
    assertEquals( "before" + PhaseId.PREPARE_UI_ROOT, log.toString() );
    log.setLength( 0 );
    lifeCycle.continueLifeCycle();
    String expected = "after"
                    + PhaseId.PREPARE_UI_ROOT
                    + "before"
                    + PhaseId.RENDER
                    + "execute"
                    + PhaseId.RENDER
                    + "after"
                    + PhaseId.RENDER;
    assertEquals( expected, log.toString() );
    log.setLength( 0 );
  }

  public void testCreateUIIfNecessary() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    int returnValue = lifeCycle.createUI();
    assertEquals( -1, returnValue );

    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, MainStartup.class );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );

    returnValue = lifeCycle.createUI();
    assertEquals( -1, returnValue );

    lifeCycle.continueLifeCycle();
    returnValue = lifeCycle.createUI();
    assertEquals( 0, returnValue );
    RWTFactory.getEntryPointManager().deregister( EntryPointUtil.DEFAULT );

    lifeCycle.continueLifeCycle();
    returnValue = lifeCycle.createUI();
    assertEquals( -1, returnValue );
  }

  public void testReadAndDispatch() {
    Display display = new Display();
    boolean returnValue = Display.getCurrent().readAndDispatch();
    assertFalse( returnValue );

    Fixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( new Runnable() {
      public void run() {
        log.append( "executed" );
      }
    } );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    returnValue = Display.getCurrent().readAndDispatch();
    assertTrue( returnValue );
    assertEquals( "executed", log.toString() );

    log.setLength( 0 );
    returnValue = Display.getCurrent().readAndDispatch();
    assertFalse( returnValue );
    assertEquals( "", log.toString() );

    Fixture.fakePhase( PhaseId.READ_DATA );
    log.setLength( 0 );
    Shell widget = new Shell( display ) {
      private static final long serialVersionUID = 1L;
      @Override
      public boolean getVisible() {
        return true;
      }
    };
    SelectionAdapter listener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        log.append( "eventExecuted" );
      }
    };
    SelectionEvent.addListener( widget, listener );
    SelectionEvent event
      = new SelectionEvent( widget, null, SelectionEvent.WIDGET_SELECTED );
    // event is scheduled but not executed at this point as there is no life
    // cycle running
    event.processEvent();
    log.setLength( 0 );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    returnValue = Display.getCurrent().readAndDispatch();
    assertTrue( returnValue );
    assertEquals( "eventExecuted", log.toString() );
  }

  public void testNestedReadAndDispatch() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    final Display display = new Display();
    Shell widget = new Shell( display ) {
      private static final long serialVersionUID = 1L;
      @Override
      public boolean getVisible() {
        return true;
      }
    };
    SelectionAdapter listener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        display.readAndDispatch();
      }
    };
    SelectionEvent.addListener( widget, listener );
    SelectionEvent event
      = new SelectionEvent( widget, null, SelectionEvent.WIDGET_SELECTED );
    event.processEvent();

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display.readAndDispatch();
    // This test ensures that nested calls of readAndDsipatch don't cause
    // an endless loop or a stack overflow - therefore no assert is needed
  }

  public void testReadAndDispatchWithAsyncExec() {
    final java.util.List<Runnable> log = new ArrayList<Runnable>();
    Runnable runnable = new Runnable() {
      public void run() {
        log.add( this );
      }
    };
    Display display = new Display();
    display.asyncExec( runnable );

    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    boolean result = display.readAndDispatch();
    assertTrue( result );
    assertSame( runnable, log.get( 0 ) );
    assertFalse( display.readAndDispatch() );
  }

  public void testBeginUIThread() throws Throwable {
    ServiceContext originContext = ContextProvider.getContext();
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    final boolean[] continueLoop = { true };
    final ServiceContext[] uiContext = new ServiceContext[ 1 ];
    final Throwable[] error = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        while( continueLoop[ 0 ] ) {
          IUIThreadHolder uiThread = ( IUIThreadHolder )Thread.currentThread();
          synchronized( uiThread.getLock() ) {
          }
          uiThread.updateServiceContext();
          uiContext[ 0 ] = ContextProvider.getContext();
          log.append( "executedInUIThread" );
          try {
            uiThread.switchThread();
          } catch( Throwable e ) {
            synchronized( error ) {
              error[ 0 ] = e;
            }
          }
        }
      }
    };
    lifeCycle.uiRunnable = runnable;
    // simulates first request
    lifeCycle.executeUIThread();
    synchronized( error ) {
      if( error[ 0 ] != null ) {
        throw error[ 0 ];
      }
    }
    assertSame( originContext, uiContext[ 0 ] );
    assertEquals( "executedInUIThread", log.toString() );
    assertTrue( getUIThread().isAlive() );
    // simulates subsequent request
    log.setLength( 0 );
    uiContext[ 0 ] = null;
    ServiceContext secondContext = newContext();
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( secondContext );
    lifeCycle.executeUIThread();
    synchronized( error ) {
      if( error[ 0 ] != null ) {
        throw error[ 0 ];
      }
    }
    assertSame( secondContext, uiContext[ 0 ] );
    assertEquals( "executedInUIThread", log.toString() );
    assertTrue( getUIThread().isAlive() );
    // simulates request that ends event loop
    UIThread endingUIThread = getUIThread();
    continueLoop[ 0 ] = false;
    lifeCycle.executeUIThread();
    synchronized( error ) {
      if( error[ 0 ] != null ) {
        throw error[ 0 ];
      }
    }
    assertFalse( endingUIThread.isAlive() );
    assertNull( getUIThread() );
    // clean up
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( originContext );
  }

  public void testUpdateServiceContext() {
    UIThread thread = new UIThread( null );
    ServiceContext firstContext = ContextProvider.getContext();
    thread.setServiceContext( firstContext );
    thread.run();
    ServiceContext secondContext = newContext();
    thread.setServiceContext( secondContext );
    thread.updateServiceContext();
    // As we don't start the UIThread, we can use the test-thread for assertion
    // instead of retrieving the actual context from inside the runnable
    assertSame( secondContext, ContextProvider.getContext() );
    // clean up
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( firstContext );
  }

  public void testUIRunnable() throws InterruptedException {
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, MainStartup.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );
    lifeCycle.addPhaseListener( new LoggingPhaseListener() );
    UIThread thread = new UIThread( lifeCycle.uiRunnable );
    thread.setServiceContext( ContextProvider.getContext() );
    thread.start();
    // TODO [rh] Find more failsafe solution
    Thread.sleep( 200 );

    String expected = "before"
                    + PhaseId.PREPARE_UI_ROOT
                    + "createUI"
                    + "after"
                    + PhaseId.PREPARE_UI_ROOT;
    assertEquals( expected, log.toString() );
  }

  public void testSleep() throws Throwable {
    final RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    final ServiceContext[] uiContext = { null };
    lifeCycle.addPhaseListener( new LoggingPhaseListener() );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      }
    } );
    final Throwable[] error = { null };
    final UIThread[] uiThread = { null };
    Runnable runnable = new Runnable() {
      public void run() {
        try {
          synchronized( uiThread[ 0 ].getLock() ) {
          }
          IUIThreadHolder uiThread = ( IUIThreadHolder )Thread.currentThread();
          uiThread.updateServiceContext();
          lifeCycle.continueLifeCycle();
          log.setLength( 0 );
          lifeCycle.sleep();
          uiContext[ 0 ] = ContextProvider.getContext();
          log.append( "readAndDispatch" );
          lifeCycle.sleep();
          log.append( "readAndDispatch" );
        } catch( Throwable e ) {
          error[ 0 ] = e;
        }
      }
    };
    uiThread[ 0 ] = new UIThread( runnable );
    LifeCycleUtil.setUIThread( ContextProvider.getSessionStore(), uiThread[ 0 ] );

    uiThread[ 0 ].setServiceContext( ContextProvider.getContext() );
    synchronized( uiThread[ 0 ].getLock() ) {
      uiThread[ 0 ].start();
      uiThread[ 0 ].switchThread();
    }

    if( error[ 0 ] != null ) {
      throw error[ 0 ];
    }
    String expected = "after" + PhaseId.PREPARE_UI_ROOT;
    assertEquals( expected, log.toString() );

    log.setLength( 0 );
    ServiceContext expectedContext = newContext();
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( expectedContext );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IPhase() {
        public PhaseId execute(Display display) throws IOException {
          log.append( "prepare" );
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PREPARE_UI_ROOT;
        }
      },
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PROCESS_ACTION;
        }
      }
    } );
    uiThread[ 0 ].setServiceContext( expectedContext );
    uiThread[ 0 ].switchThread();

    if( error[ 0 ] != null ) {
      throw error[ 0 ];
    }
    expected = "before"
             + PhaseId.PREPARE_UI_ROOT
             + "prepare"
             + "after"
             + PhaseId.PREPARE_UI_ROOT
             + "before"
             + PhaseId.PROCESS_ACTION
             + "readAndDispatch"
             + "after"
             + PhaseId.PROCESS_ACTION;
    assertEquals( expected, log.toString() );
    assertSame( expectedContext, uiContext[ 0 ] );

    log.setLength( 0 );
    lifeCycle.setPhaseOrder( new IPhase[] {
      new IInterruptible() {
        public PhaseId execute(Display display) throws IOException {
          return null;
        }
        public PhaseId getPhaseId() {
          return PhaseId.PROCESS_ACTION;
        }
      }
    } );
    uiThread[ 0 ].switchThread();
    if( error[ 0 ] != null ) {
      throw error[ 0 ];
    }
    expected = "before"
             + PhaseId.PROCESS_ACTION
             + "readAndDispatch";
    assertEquals( expected, log.toString() );
    assertFalse( uiThread[ 0 ].isAlive() );
  }

  public void testGetSetPhaseOrder() {
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    IPhase[] phaseOrder = new IPhase[ 0 ];
    lifeCycle.setPhaseOrder( phaseOrder );
    assertSame( phaseOrder, lifeCycle.getPhaseOrder() );
    // create new context to ensure that phase order is stored in context
    ServiceContext bufferedContext = ContextProvider.getContext();
    ContextProvider.releaseContextHolder();
    Fixture.createServiceContext();
    assertNull( lifeCycle.getPhaseOrder() );
    // clean up
    ContextProvider.releaseContextHolder();
    ContextProvider.setContext( bufferedContext );
  }

  public void testErrorHandlingInCreateUI() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, ErrorStartup.class );
    try {
      ( ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle() ).execute();
      fail();
    } catch( RuntimeException re ) {
      assertEquals( ERR_MSG, re.getMessage() );
    }
  }

  public void testSessionInvalidateWithRunningEventLoop() throws Throwable {
    final ISessionStore session = ContextProvider.getSessionStore();
    final String[] invalidateThreadName = { null };
    final boolean hasContext[] = new boolean[]{ false };
    final IServiceStore serviceStore[] =  { null };
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( SessionStoreEvent event ) {
        invalidateThreadName[ 0 ] = Thread.currentThread().getName();
        hasContext[ 0 ] = ContextProvider.hasContext();
        serviceStore[ 0 ] = ContextProvider.getServiceStore();
      }
    } );
    // Register and 'run' entry point with readAndDispatch/sleep loop
    Class<? extends IEntryPoint> entryPointClass = SessionInvalidateWithEventLoopEntryPoint.class;
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, entryPointClass );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();
    // Store some values for later comparison
    IUIThreadHolder uiThreadHolder = LifeCycleUtil.getUIThread( session );
    String uiThreadName = uiThreadHolder.getThread().getName();
    // Invalidate session
    invalidateSession( session );
    //
    assertFalse( uiThreadHolder.getThread().isAlive() );
    assertFalse( session.isBound() );
    assertEquals( invalidateThreadName[ 0 ], uiThreadName );
    assertTrue( hasContext[ 0 ] );
    assertNotNull( serviceStore[ 0 ] );
    assertEquals( "", log.toString() );
  }

  public void testExceptionInRender() throws Exception {
    Fixture.fakeRequestParam( RequestParams.STARTUP, EntryPointUtil.DEFAULT );
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Class<? extends IEntryPoint> entryPointClass = ExceptionInRenderEntryPoint.class;
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, entryPointClass );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    try {
      lifeCycle.execute();
      fail( "Exception in render must be re-thrown by life cycle" );
    } catch( Throwable e ) {
      assertEquals( EXCEPTION_IN_RENDER, e.getMessage() );
    }
  }

  public void testSessionInvalidateWithoutRunningEventLoop() throws Throwable {
    final ISessionStore session = ContextProvider.getSessionStore();
    final String[] uiThreadName = { "unknown-ui-thread" };
    final String[] invalidateThreadName = { "unkown-invalidate-thread" };
    final boolean hasContext[] = new boolean[]{ false };
    final IServiceStore serviceStore[] = { null };
    session.addSessionStoreListener( new SessionStoreListener() {
      public void beforeDestroy( SessionStoreEvent event ) {
        invalidateThreadName[ 0 ] = Thread.currentThread().getName();
        hasContext[ 0 ] = ContextProvider.hasContext();
        serviceStore[ 0 ] = ContextProvider.getServiceStore();
      }
    } );
    // Register and 'run' entry point with readAndDispatch/sleep loop
    Class<? extends IEntryPoint> entryPoint = SessionInvalidateWithoutEventLoopEntryPoint.class;
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, entryPoint );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public void beforePhase( PhaseEvent event ) {
        uiThreadName[ 0 ] = Thread.currentThread().getName();
      }
      public void afterPhase( PhaseEvent event ) {
      }
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
    } );
    lifeCycle.execute();
    // Invalidate session
    invalidateSession( session );
    //
    assertFalse( session.isBound() );
    assertEquals( uiThreadName[ 0 ], invalidateThreadName[ 0 ] );
    assertTrue( hasContext[ 0 ] );
    assertNotNull( serviceStore[ 0 ] );
  }

  public void testDisposeDisplayOnSessionTimeout() throws Throwable {
    final ISessionStore session = ContextProvider.getSessionStore();
    Class<? extends IEntryPoint> clazz = DisposeDisplayOnSessionTimeoutEntryPoint.class;
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, clazz );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();
    invalidateSession( session );
    assertEquals( "display disposed", log.toString() );
  }

  public void testOrderOfDisplayDisposeAndSessionUnbound() throws Throwable {
    final ISessionStore session = ContextProvider.getSessionStore();
    Class<? extends IEntryPoint> clazz = TestOrderOfDisplayDisposeAndSessionUnboundEntryPoint.class;
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, clazz );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();
    invalidateSession( session );
    assertEquals( "disposeEvent, beforeDestroy", log.toString() );
  }

  public void testSwitchThreadCannotBeInterrupted() throws Exception {
    final Throwable[] errorInUIThread = { new Exception( "did not run" ) };
    final UIThread[] uiThread = { null };
    uiThread[ 0 ] = new UIThread( new Runnable() {
      public void run() {
        try {
          synchronized( errorInUIThread ) {
            errorInUIThread[ 0 ] = null;
          }
          uiThread[ 0 ].switchThread();
        } catch( Throwable t ) {
          synchronized( errorInUIThread ) {
            errorInUIThread[ 0 ] = t;
          }
        }
      }
    } );
    uiThread[ 0 ].start();
    Thread.sleep( 100 );
    uiThread[ 0 ].interrupt();
    synchronized( errorInUIThread ) {
      assertNull( "switchThread must not unblock when thread is interrupted",
                  errorInUIThread[ 0 ] );
    }
    // unblock ui thread, see bug 351277
    synchronized( uiThread[ 0 ].getLock() ) {
      uiThread[ 0 ].getLock().notifyAll();
    }
  }

  public void testGetUIThreadWhileLifeCycleInExecute() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, TestEntryPoint.class );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    final Thread[] currentThread = { null };
    final Thread[] uiThread = { null };
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public PhaseId getPhaseId() {
        return PhaseId.PREPARE_UI_ROOT;
      }
      public void beforePhase( PhaseEvent event ) {
      }
      public void afterPhase( PhaseEvent event ) {
        currentThread[ 0 ] = Thread.currentThread();
        uiThread[ 0 ] = LifeCycleUtil.getUIThread( ContextProvider.getSessionStore() ).getThread();
      }
    } );

    lifeCycle.execute();

    assertSame( currentThread[ 0 ], uiThread[ 0 ] );
  }

  public void testGetUIThreadAfterLifeCycleExecuted() throws IOException {
    RWTFactory.getEntryPointManager().register( EntryPointUtil.DEFAULT, TestEntryPoint.class );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.execute();

    Thread uiThread = LifeCycleUtil.getUIThread( ContextProvider.getSessionStore() ).getThread();

    assertNotNull( uiThread );
  }

  private static void invalidateSession( final ISessionStore session ) throws Throwable {
    Runnable runnable = new Runnable() {
      public void run() {
        session.getHttpSession().invalidate();
      }
    };
    Fixture.runInThread( runnable );
  }

  private static ServiceContext newContext() {
    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
    ServiceContext result = new ServiceContext( request, response );
    result.setServiceStore( new ServiceStore() );
    return result;
  }

  private static void newSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
  }

  private static UIThread getUIThread() {
    ISessionStore session = ContextProvider.getSessionStore();
    return ( UIThread )LifeCycleUtil.getUIThread( session );
  }

  private static class LoggingPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;
    public void beforePhase( PhaseEvent event ) {
      log.append( "before" + event.getPhaseId() );
    }
    public void afterPhase( PhaseEvent event ) {
      log.append( "after" + event.getPhaseId() );
    }
    public PhaseId getPhaseId() {
      return PhaseId.ANY;
    }
  }

  public static final class MainStartup implements IEntryPoint {
    public int createUI() {
      log.append( "createUI" );
      return 0;
    }
  }

  public static final class ErrorStartup implements IEntryPoint {
    public int createUI() {
      throw new RuntimeException( ERR_MSG );
    }
  }

  private final class ExceptionListenerTest implements PhaseListener {

    private static final long serialVersionUID = 1L;

    public void afterPhase( PhaseEvent event ) {
      log.append( AFTER + event.getPhaseId() + "|" );
      throw new RuntimeException();
    }

    public void beforePhase( PhaseEvent event ) {
      log.append( BEFORE + event.getPhaseId() + "|" );
      throw new RuntimeException();
    }

    public PhaseId getPhaseId() {
      return PhaseId.PREPARE_UI_ROOT;
    }
  }

  public static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      new Display();
      return 0;
    }
  }

  public static class TestPhasesEntryPoint implements IEntryPoint {
    public int createUI() {
      Display display = new Display();
      while( !display.isDisposed() ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      return 0;
    }
  }

  public static class TestErrorInLifeCycleEntryPoint implements IEntryPoint {
    public int createUI() {
      String msg = TestErrorInLifeCycleEntryPoint.class.getName();
      throw new RuntimeException( msg );
    }
  }

  public static class TestEntryPointWithLog implements IEntryPoint {
    public int createUI() {
      new Display();
      log.append( DISPLAY_CREATED );
      return 0;
    }
  }

  public static class DisposeDisplayOnSessionTimeoutEntryPoint
    implements IEntryPoint
  {
    public int createUI() {
      Display display = new Display();
      display.addListener( SWT.Dispose, new Listener() {
        public void handleEvent( Event event ) {
          log.append( "display disposed" );
        }
      } );
      return 0;
    }
  }

  public static class SessionInvalidateWithEventLoopEntryPoint
    implements IEntryPoint
  {
    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display );
      shell.open();
      while( !shell.isDisposed() ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      log.append( "regular end of createUI" );
      return 0;
    }
  }

  public static class SessionInvalidateWithoutEventLoopEntryPoint
    implements IEntryPoint
  {
    public int createUI() {
      new Display();
      return 0;
    }
  }

  public static class ExceptionInRenderEntryPoint implements IEntryPoint {
    public static class BuggyShell extends Shell {
      private static final long serialVersionUID = 1L;
      public BuggyShell( Display display ) {
        super( display );
      }
      @SuppressWarnings("unchecked")
      @Override
      public <T> T getAdapter( Class<T> adapter ) {
        Object result;
        if( adapter.equals( ILifeCycleAdapter.class ) ) {
          result = new AbstractWidgetLCA() {
            @Override
            public void preserveValues( Widget widget ) {
            }
            public void readData( Widget widget ) {
            }
            @Override
            public void renderInitialization( Widget widget )
              throws IOException
            {
              throw new RuntimeException( EXCEPTION_IN_RENDER );
            }
            @Override
            public void renderChanges( Widget widget ) throws IOException {
              throw new RuntimeException( EXCEPTION_IN_RENDER );
            }
            @Override
            public void renderDispose( Widget widget ) throws IOException {
            }
          };
        } else {
          result = super.getAdapter( adapter );
        }
        return ( T )result;
      }
    }

    public int createUI() {
      Display display = new Display();
      Shell shell = new BuggyShell( display );
      shell.open();
      while( !shell.isDisposed() ) {
        try {
          if( !display.readAndDispatch() ) {
            display.sleep();
          }
        } catch( RuntimeException e ) {
          // continue loop
        }
      }
      log.append( "regular end of createUI" );
      return 0;
    }
  }

  public static final class TestOrderOfDisplayDisposeAndSessionUnboundEntryPoint
    implements IEntryPoint
  {
    public int createUI() {
      Display display = new Display();
      display.addListener( SWT.Dispose, new Listener() {
        public void handleEvent( Event event ) {
          log.append( "disposeEvent, " );
        }
      } );
      ISessionStore sessionStore = RWT.getSessionStore();
      sessionStore.addSessionStoreListener( new SessionStoreListener() {
        public void beforeDestroy( SessionStoreEvent event ) {
          log.append( "beforeDestroy" );
        }
      } );
      return 0;
    }
  }
}
