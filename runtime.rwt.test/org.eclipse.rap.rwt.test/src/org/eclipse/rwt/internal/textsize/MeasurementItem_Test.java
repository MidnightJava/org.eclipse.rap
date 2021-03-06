/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public class MeasurementItem_Test extends TestCase {
  private static final FontData FONT_DATA = new FontData( "arial", 12, SWT.BOLD );
  private static final int MODE = TextSizeUtil.STRING_EXTENT;

  public void testMeasurementItemCreation() {
    MeasurementItem item = createItem( "text", FONT_DATA, 13, MODE );

    assertSame( FONT_DATA, item.getFontData() );
    assertSame( "text", item.getTextToMeasure() );
    assertEquals( 13, item.getWrapWidth() );
    assertEquals( MODE, item.getMode() );
  }

  public void testEquals() {
    int otherMode = TextSizeUtil.TEXT_EXTENT;
    FontData otherFontData = new FontData( "helvetia", 12, SWT.BOLD );
    MeasurementItem item1 = createItem( "text", FONT_DATA, 13, MODE );

    assertTrue( item1.equals( item1 ) );
    assertFalse( item1.equals( null ) );
    assertFalse( item1.equals( new Object() ) );
    assertFalse( item1.equals( createItem( "otherText", FONT_DATA, 13, MODE ) ) );
    assertFalse( item1.equals( createItem( "text", otherFontData, 13, MODE ) ) );
    assertFalse( item1.equals( createItem( "text", FONT_DATA, 155, MODE ) ) );
    assertFalse( item1.equals( createItem( "text", FONT_DATA, 13, otherMode ) ) );
    assertTrue( item1.equals( createItem( "text", FONT_DATA, 13, MODE ) ) );
  }

  public void testHashCode() {
    MeasurementItem item = createItem( "text", FONT_DATA, 13, MODE );

    int hashCode = item.hashCode();

    assertEquals( 1959330623, hashCode );
  }

  public void testParamTextToMeasureMustNotBeNull() {
    try {
      new MeasurementItem( null, FONT_DATA, SWT.DEFAULT, MODE );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testParamFontDataMustNotBeNull() {
    try {
      new MeasurementItem( "text", null, SWT.DEFAULT, MODE );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testIsSerializable() throws Exception {
    MeasurementItem measurementItem = createItem( "text", FONT_DATA, 155, MODE );

    MeasurementItem deserialized = Fixture.serializeAndDeserialize( measurementItem );

    assertEquals( measurementItem.getTextToMeasure(), deserialized.getTextToMeasure() );
    assertEquals( measurementItem.getFontData(), deserialized.getFontData() );
    assertEquals( measurementItem.getWrapWidth(), deserialized.getWrapWidth() );
  }

  private MeasurementItem createItem( String textToMeasure,
                                      FontData fontData,
                                      int wrapWidth,
                                      int mode )
  {
    return new MeasurementItem( textToMeasure, fontData, wrapWidth, mode );
  }
}
