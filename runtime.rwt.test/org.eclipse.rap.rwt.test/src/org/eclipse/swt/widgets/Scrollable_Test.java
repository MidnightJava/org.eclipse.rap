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
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;


public class Scrollable_Test extends TestCase {

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testComputeTrim() {
    Composite scrollable = new Composite( shell, SWT.BORDER );
    Rectangle trim = scrollable.computeTrim( 20, 30, 200, 300 );
    int borderWidth = scrollable.getBorderWidth();
    assertEquals( 20 - borderWidth, trim.x );
    assertEquals( 30 - borderWidth, trim.y );
    assertEquals( 200 + ( 2 * borderWidth ), trim.width );
    assertEquals( 300 + ( 2 * borderWidth ), trim.height );
  }
  
  public void testComputeTrimWithPadding() {
    final Rectangle padding = new Rectangle( 10, 10, 10, 10 );
    Composite scrollable = new Composite( shell, SWT.BORDER ) {
      int getVScrollBarWidth() {
        return 20;
      }
      int getHScrollBarHeight() {
        return 20;
      }
      Rectangle getPadding() {
        return padding;
      }
    };
    int borderWidth = scrollable.getBorderWidth();
    Rectangle trim = scrollable.computeTrim( 20, 30, 200, 300 );
    assertEquals( 20 - padding.x - borderWidth, trim.x );
    assertEquals( 30 - padding.y - borderWidth, trim.y );
    assertEquals( 232, trim.width );
    assertEquals( 332, trim.height );
  }

  public void testGetClientArea() {
    Composite scrollable = new Composite( shell, SWT.BORDER );
    scrollable.setSize( 100, 100 );
    Rectangle expected = new Rectangle( 0, 0, 98, 98 );
    assertEquals( expected, scrollable.getClientArea() );
  }
  
  public void testClientAreaWithPadding() {
    Composite scrollable = new Composite( shell, SWT.BORDER ) {
      int getVScrollBarWidth() {
        return 20;
      }
      int getHScrollBarHeight() {
        return 20;
      }
      Rectangle getPadding() {
        return new Rectangle( 10, 10, 10, 10 );
      }
    };
    scrollable.setSize( 100, 100 );
    assertEquals( 1, scrollable.getBorderWidth() );
    Rectangle expected = new Rectangle( 10, 10, 68, 68 );
    assertEquals( expected, scrollable.getClientArea() );
  }

  public void testClientAreaIsZero() {
    Composite scrollable = new Composite( shell, SWT.BORDER );
    scrollable.setSize( 0, 0 );
    Rectangle expected = new Rectangle( 0, 0, 0, 0 );
    assertEquals( expected, scrollable.getClientArea() );
  }
  
  public void testClientAreaIsZeroWithPadding() {
    Composite scrollable = new Composite( shell, SWT.BORDER ) {
      int getVScrollBarWidth() {
        return 20;
      }
      int getHScrollBarHeight() {
        return 20;
      }
      Rectangle getPadding() {
        return new Rectangle( 10, 10, 10, 10 );
      }
    };
    scrollable.setSize( 25, 25 );
    Rectangle expected = new Rectangle( 10, 10, 0, 0 );
    assertEquals( expected, scrollable.getClientArea() );
  }
  
  public void testScrollBarsAreDisposed() {
    Composite scrollable = new Composite( shell, SWT.V_SCROLL | SWT.H_SCROLL );
    ScrollBar verticalBar = scrollable.getVerticalBar();
    ScrollBar horizontalBar = scrollable.getHorizontalBar();
    
    scrollable.dispose();
    
    assertTrue( verticalBar.isDisposed() );
    assertTrue( horizontalBar.isDisposed() );
  }
  
  public void testDisposeWithoutScrollBars() {
    Composite scrollable = new Composite( shell, SWT.NONE );

    scrollable.dispose();
    
    assertTrue( scrollable.isDisposed() );
  }

}
