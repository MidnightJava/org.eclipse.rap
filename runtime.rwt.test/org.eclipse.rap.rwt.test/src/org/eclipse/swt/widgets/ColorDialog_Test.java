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
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class ColorDialog_Test extends TestCase {

  private Display display;
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testRGB() {
    RGB rgb = new RGB( 255, 0, 0 );
    ColorDialog dialog = new ColorDialog( shell );
    dialog.setRGB( rgb );
    assertEquals( rgb, dialog.getRGB() );
  }

  public void testInitialRGBValue() {
    ColorDialog dialog = new ColorDialog( shell );
    assertNull( dialog.getRGB() );
  }

  public void testOpen_JEE_COMPATIBILITY() {
    // Activate SimpleLifeCycle
    RWTFactory.getLifeCycleFactory().deactivate();
    RWTFactory.getLifeCycleFactory().activate();
    RGB rgb = new RGB( 255, 0, 0 );
    ColorDialog dialog = new ColorDialog( shell );
    dialog.setRGB( rgb );

    try {
      dialog.open();
      fail();
    } catch( UnsupportedOperationException expected ) {
      assertEquals( "Method not supported in JEE_COMPATIBILITY mode.", expected.getMessage() );
    }
  }
}
