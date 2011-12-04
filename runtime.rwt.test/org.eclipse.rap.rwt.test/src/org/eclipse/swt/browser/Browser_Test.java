/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.browser;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class Browser_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testInitialValues() {
    Browser browser = new Browser( shell, SWT.NONE );

    assertEquals( "", browser.getUrl() );
    assertEquals( "", getText( browser ) );
  }
  
  public void testMozillaStyleFlags() {
    try {
      new Browser( shell, SWT.MOZILLA );
      fail( "SWT.MOZILLA not allowed" );
    } catch( SWTError error ) {
      assertEquals( SWT.ERROR_NO_HANDLES, error.code);
      assertEquals( "Unsupported Browser type", error.getMessage() );
    }
  }
  
  public void testWebkitStyleFlag() {
    try {
      new Browser( shell, SWT.WEBKIT );
      fail( "SWT.WEBKIT not allowed" );
    } catch( SWTError error ) {
      assertEquals( SWT.ERROR_NO_HANDLES, error.code);
      assertEquals( "Unsupported Browser type", error.getMessage() );
    }
  }

  public void testUrlAndText() {
    Browser browser = new Browser( shell, SWT.NONE );

    browser.setUrl( "http://eclipse.org/rap" );
    assertEquals( "", getText( browser ) );
    browser.setText( "<html></head>..." );
    assertEquals( "", browser.getUrl() );

    try {
      browser.setUrl( "oldValue" );
      browser.setUrl( null );
      fail( "Browser#setUrl: null not allowed" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldValue", browser.getUrl() );
    }
    try {
      browser.setText( "oldValue" );
      browser.setText( null );
      fail( "Browser#setText: null not allowed" );
    } catch( IllegalArgumentException e ) {
      assertEquals( "oldValue", getText( browser ) );
    }
  }

  public void testLocationEvent() {
    final StringBuilder log = new StringBuilder();
    final String[] expectedLocation = new String[ 1 ];
    final Browser browser = new Browser( shell, SWT.NONE );
    LocationListener listener = new LocationListener() {
      public void changing( final LocationEvent event ) {
        log.append( "changing" + event.location + "|" );
        assertEquals( expectedLocation[ 0 ], event.location );
        assertFalse( event.top );
      }
      public void changed( final LocationEvent event ) {
        log.append( "changed" + event.location );
        assertSame( browser, event.getSource() );
        assertEquals( true, event.doit );
        assertEquals( expectedLocation[ 0 ], event.location );
        assertTrue( event.top );
      }
    };
    LocationListener vetoListener = new LocationListener() {
      public void changing( final LocationEvent event ) {
        log.append( "changing" + event.location + "|" );
        event.doit = false;
      }
      public void changed( final LocationEvent event ) {
        log.append( "changed" + event.location );
      }
    };

    // test basic event behaviour with setUrl
    browser.addLocationListener( listener );
    expectedLocation[ 0 ] = "NEW_URL";
    boolean success = browser.setUrl( expectedLocation[ 0 ] );
    assertEquals( true, success );
    assertEquals( "changingNEW_URL|changedNEW_URL", log.toString() );
    // setting the current url must also fire events
    log.setLength( 0 );
    success = browser.setUrl( expectedLocation[ 0 ] );
    assertEquals( true, success );
    assertEquals( "changingNEW_URL|changedNEW_URL", log.toString() );
    // clean up
    log.setLength( 0 );
    browser.removeLocationListener( listener );

    // test vetoing listener with with setUrl
    browser.setUrl( "OLD_URL" );
    browser.addLocationListener( vetoListener );
    success = browser.setUrl( "NEW_URL" );
    assertEquals( false, success );
    assertEquals( "changingNEW_URL|", log.toString() );
    assertEquals( "OLD_URL", browser.getUrl() );
    // clean up
    log.setLength( 0 );
    browser.removeLocationListener( vetoListener );

    // test basic event behaviour with setText
    browser.addLocationListener( listener );
    expectedLocation[ 0 ] = "about:blank";
    success = browser.setText( "Some html" );
    assertEquals( true, success );
    assertEquals( "changingabout:blank|changedabout:blank", log.toString() );
    // setting the current url must also fire events
    log.setLength( 0 );
    success = browser.setText( "Some html" );
    assertEquals( true, success );
    assertEquals( "changingabout:blank|changedabout:blank", log.toString() );
    // clean up
    log.setLength( 0 );
    browser.removeLocationListener( listener );

    // test vetoing listener with with setText
    browser.setText( "Old html" );
    browser.addLocationListener( vetoListener );
    success = browser.setText( "New html" );
    assertEquals( false, success );
    assertEquals( "changingabout:blank|", log.toString() );
    assertEquals( "Old html", getText( browser ) );
    // clean up
    log.setLength( 0 );
    browser.removeLocationListener( vetoListener );
  }

  public void testProgressEvent_setTextAllowed() {
    final ArrayList<String> log = new ArrayList<String>();
    final Browser browser = new Browser( shell, SWT.NONE );
    browser.addProgressListener( new ProgressListener() {
      public void changed( final ProgressEvent event ) {
        log.add( "changed" );
      }
      public void completed( final ProgressEvent event ) {
        log.add( "completed" );
      }
    } );
    browser.setText( "test" );
    assertEquals( 1, log.size() );
    assertEquals( "changed", log.get( 0 ) );
  }

  public void testProgressEvent_setTextNotAllowed() {
    final ArrayList<String> log = new ArrayList<String>();
    final Browser browser = new Browser( shell, SWT.NONE );
    browser.addLocationListener( new LocationListener() {
      public void changing( final LocationEvent event ) {
        event.doit = false;
      }
      public void changed( final LocationEvent event ) {
      }
    } );
    browser.addProgressListener( new ProgressListener() {
      public void changed( final ProgressEvent event ) {
        log.add( "changed" );
      }
      public void completed( final ProgressEvent event ) {
        log.add( "completed" );
      }
    } );
    browser.setText( "test" );
    assertEquals( 0, log.size() );
  }

  public void testProgressEvent_setUrlAllowed() {
    final ArrayList<String> log = new ArrayList<String>();
    final Browser browser = new Browser( shell, SWT.NONE );
    browser.addProgressListener( new ProgressListener() {
      public void changed( final ProgressEvent event ) {
        log.add( "changed" );
      }
      public void completed( final ProgressEvent event ) {
        log.add( "completed" );
      }
    } );
    browser.setUrl( "http://www.eclipse.org" );
    assertEquals( 1, log.size() );
    assertEquals( "changed", log.get( 0 ) );
  }

  public void testProgressEvent_setUrlNotAllowed() {
    final ArrayList<String> log = new ArrayList<String>();
    final Browser browser = new Browser( shell, SWT.NONE );
    browser.addLocationListener( new LocationListener() {
      public void changing( final LocationEvent event ) {
        event.doit = false;
      }
      public void changed( final LocationEvent event ) {
      }
    } );
    browser.addProgressListener( new ProgressListener() {
      public void changed( final ProgressEvent event ) {
        log.add( "changed" );
      }
      public void completed( final ProgressEvent event ) {
        log.add( "completed" );
      }
    } );
    browser.setUrl( "http://www.eclipse.org" );
    assertEquals( 0, log.size() );
  }

  public void testGetWebBrowser() {
    Browser browser = new Browser( shell, SWT.NONE );
    assertNull( browser.getWebBrowser() );
  }
  
  public void testIsSerializable() throws Exception {
    Browser browser = new Browser( shell, SWT.NONE );
    browser.setUrl( "http://eclipse.org/rap" );
    
    Browser deserializedBrowser = Fixture.serializeAndDeserialize( browser );
    
    assertEquals( browser.getUrl(), deserializedBrowser.getUrl() );
  }

  private static String getText( final Browser browser ) {
    Object adapter = browser.getAdapter( IBrowserAdapter.class );
    IBrowserAdapter browserAdapter = ( IBrowserAdapter )adapter;
    return browserAdapter.getText();
  }
}
