/*******************************************************************************
 * Copyright (c) 2009, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.custom;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.custom.IStyledTextAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class StyledText_Test extends TestCase {
  private static final String TEXT = "Eclipse is an open source community.";

  private StyledText styledText;


  public void testInitialValues() {
    assertEquals( "", styledText.getText() );
    assertEquals( 0, styledText.getCaretOffset() );
    assertEquals( new Point( 0, 0 ), styledText.getSelection() );
    assertEquals( 0, styledText.getStyleRanges().length );
  }

  public void testText() {
    styledText.setText( TEXT );
    assertEquals( TEXT, styledText.getText() );
  }
  
  public void testSetTextWithNullArgument() {
    try {
      styledText.setText( null );
      fail( "Must not allow to set null-text." );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testGetCharCount() {
    styledText.setText( TEXT );
    assertEquals( TEXT.length(), styledText.getCharCount() );
  }

  public void testSelectionText() {
    styledText.setText( TEXT );
    styledText.setSelection( 14, 25 );
    assertEquals( "open source", styledText.getSelectionText() );
  }

  public void testSelection() {
    styledText.setText( TEXT );
    styledText.setSelection( 0, 7 );
    assertEquals( new Point( 0, 7 ), styledText.getSelection() );
    styledText.setSelection( 10, 300 );
    assertEquals( new Point( 10, 36 ), styledText.getSelection() );
  }

  public void testCaretOffset() {
    styledText.setText( TEXT );
    styledText.setCaretOffset( 10 );
    assertEquals( 10, styledText.getCaretOffset() );
    styledText.setCaretOffset( 300 );
    assertEquals( 36, styledText.getCaretOffset() );
  }
  
  public void testGetStyleRange() {
    styledText.setText( TEXT );
    assertEquals( 0, styledText.getStyleRanges().length );
  }

  public void testStyleRange() {
    styledText.setText( TEXT );
    
    StyleRange sr = new StyleRange();
    sr.start = 10;
    sr.length = 10;
    sr.fontStyle = SWT.BOLD;
    styledText.setStyleRange( sr );
    assertEquals( 1, styledText.getStyleRanges().length );
    
    StyleRange sr1 = new StyleRange();
    sr1.start = 12;
    sr1.length = 6;
    sr1.fontStyle = SWT.ITALIC;
    styledText.setStyleRange( sr1 );
    assertEquals( 3, styledText.getStyleRanges().length );
  }
  
  public void testSetStyleRangeWithOutOfBoundsValue() {
    StyleRange styleRange = new StyleRange();
    styleRange.start = 12;
    styleRange.length = 100;
    styleRange.fontStyle = SWT.ITALIC;
    try {
      styledText.setStyleRange( styleRange );
      fail( "Must not allow to set range out of text bounds." );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testAdapter() {
    styledText.setText( TEXT );
    IStyledTextAdapter adapter = getStypedTextAdapter( styledText );
    String html = adapter.getHtml();
    String expected
      = "<span id=sr0></span><span id=sel></span>"
      + "<span id=sr0>Eclipse&nbsp;is&nbsp;an&nbsp;open&nbsp;source&nbsp;community.</span>";
    assertEquals( expected, html );
  }

  public void testGenerateHtml() {
    styledText.setText( TEXT );
    IStyledTextAdapter adapter = getStypedTextAdapter( styledText );
    StyleRange sr = new StyleRange();
    sr.start = 10;
    sr.length = 10;
    sr.fontStyle = SWT.BOLD;
    styledText.setStyleRange( sr );
    String html = adapter.getHtml();
    String expected = "<span id=sr0></span><span id=sel></span>"
      + "<span id=sr0>Eclipse&nbsp;is</span>"
      + "<span id=sr10 style='font-weight:bold;'>&nbsp;an&nbsp;open&nbsp;s</span>"
      + "<span id=sr20>ource&nbsp;community.</span>";
    assertEquals( expected, html );
    styledText.setSelection( 14, 25 );
    html = adapter.getHtml();
    expected = "<span id=sr0>Eclipse&nbsp;is</span>"
      + "<span id=sr10 style='font-weight:bold;'>&nbsp;an&nbsp;</span>"
      + "<span id=sel><span id=sr14 style='font-weight:bold;'>open&nbsp;s</span>"
      + "<span id=sr20>ource</span></span><span id=sr25>&nbsp;community.</span>";
    assertEquals( expected, html );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display );
    styledText = new StyledText( shell, SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private static IStyledTextAdapter getStypedTextAdapter( StyledText styledText ) {
    return ( IStyledTextAdapter )styledText.getAdapter( IStyledTextAdapter.class );
  }
}
