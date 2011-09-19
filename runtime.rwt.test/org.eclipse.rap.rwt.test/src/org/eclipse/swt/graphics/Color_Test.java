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
package org.eclipse.swt.graphics;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class Color_Test extends TestCase {

  private Device device;

  public void testColorFromRGB() {
    Color salmon = Graphics.getColor( new RGB( 250, 128, 114 ) );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
  }

  public void testColorFromInt() {
    Color salmon = Graphics.getColor( 250, 128, 114 );
    assertEquals( 250, salmon.getRed() );
    assertEquals( 128, salmon.getGreen() );
    assertEquals( 114, salmon.getBlue() );
  }

  public void testColorFromConstant() {
    Color color = device.getSystemColor( SWT.COLOR_RED );
    assertEquals( 255, color.getRed() );
    assertEquals( 0, color.getGreen() );
    assertEquals( 0, color.getBlue() );
  }

  public void testEquality() {
    Color salmon1 = Graphics.getColor( 250, 128, 114 );
    Color salmon2 = Graphics.getColor( 250, 128, 114 );
    Color chocolate = Graphics.getColor( 210, 105, 30 );
    assertTrue( salmon1.equals( salmon2 ) );
    assertFalse( salmon1.equals( chocolate ) );
    salmon1 = new Color( device, 250, 128, 114 );
    salmon2 = new Color( device, 250, 128, 114 );
    assertTrue( salmon1.equals( salmon2 ) );
    salmon1 = new Color( device, 250, 128, 114 );
    salmon2 = Graphics.getColor( 250, 128, 114 );
    assertTrue( salmon1.equals( salmon2 ) );
  }

  public void testIdentity() {
    Color salmon1 = Graphics.getColor( 250, 128, 114 );
    Color salmon2 = Graphics.getColor( 250, 128, 114 );
    assertSame( salmon1, salmon2 );
    salmon1 = new Color( device, 250, 128, 114 );
    salmon2 = Graphics.getColor( 250, 128, 114 );
    assertNotSame( salmon1, salmon2 );
  }
  
  public void testGetRGB() {
    RGB rgbSalmon = new RGB( 250, 128, 114 );
    assertEquals( rgbSalmon, Graphics.getColor( rgbSalmon ).getRGB() );
  }
  
  public void testConstructor() {
    Color color = new Color( null, 0, 0, 0 );
    assertSame( Display.getCurrent(), color.getDevice() );
    color = new Color( null, new RGB( 0, 0, 0 ) );
    assertSame( Display.getCurrent(), color.getDevice() );
  }
  
  public void testConstructorWithoutDevice() {
    device.dispose();
    try {
      new Color( null, new RGB( 0, 0, 0 ) );
      fail( "Must provide device for color constructor" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
    try {
      new Color( null, 0, 0, 0 );
      fail( "Must provide device for color constructor" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testConstructorWithInvalidRedValue() {
    try {
      new Color( device, -1, 0, 0 );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
    try {
      new Color( device, 300, 0, 0 );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testConstructorWithInvalidGreenValue() {
    try {
      new Color( device, 0, -1, 0 );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
    try {
      new Color( device, 0, 300, 0 );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testConstructorWithInvalidBlueValue() {
    try {
      new Color( device, 0, 0, -1 );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
    try {
      new Color( device, 0, 0, 300 );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testDispose() {
    Color color = new Color( device, new RGB( 0, 0, 0 ) );
    color.dispose();
    assertTrue( color.isDisposed() );
  }
  
  public void testDisposeFactoryCreated() {
    Color color = Graphics.getColor( new RGB( 0, 0, 0 ) );
    try {
      color.dispose();
      fail( "It is not allowed to dispose of a factory-created color" );
    } catch( IllegalStateException e ) {
      assertFalse( color.isDisposed() );
    }
  }
  
  public void testGetAttributesAfterDispose() {
    Color font = new Color( device, 0, 0, 0 );
    font.dispose();
    try {
      font.getRed();
      fail( "Must not allow to access attributes of disposed color" );
    } catch( Exception expected ) {
    }
    try {
      font.getGreen();
      fail( "Must not allow to access attributes of disposed color" );
    } catch( Exception expected ) {
    }
    try {
      font.getBlue();
      fail( "Must not allow to access attributes of disposed color" );
    } catch( Exception expected ) {
    }
    try {
      font.getRGB();
      fail( "Must not allow to access attributes of disposed color" );
    } catch( Exception expected ) {
    }
  }
  
  public void testSerializeSessionColor() throws Exception {
    Color color = new Color( device, 1, 2, 3 );
    
    Color deserializedColor = Fixture.serializeAndDeserialize( color );

    assertEquals( color.getRGB(), deserializedColor.getRGB() );
    assertFalse( deserializedColor.isDisposed() );
    assertNotNull( deserializedColor.getDevice() );
    assertNotSame( color.getDevice(), deserializedColor.getDevice() );
  }
  
  public void testSerializeSharedColor() throws Exception {
    Color color = Graphics.getColor( 1, 2, 3 );
    
    Color deserializedColor = Fixture.serializeAndDeserialize( color );
    
    assertEquals( color.getRGB(), deserializedColor.getRGB() );
    assertFalse( deserializedColor.isDisposed() );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    device = new Display();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
