/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.graphics;

import java.io.InputStream;

import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;


/**
 * This is a helper class for operations with text, fonts, colors and images.
 * As RAP needs to handle with multiple clients simultaneously there are no
 * constructors for these objects to share them between different sessions
 * in order to have a much smaller memory footprint.
 *
 * @since 1.0
 */
public final class Graphics {

  /**
   * Returns an instance of {@link Color} given an
   * <code>RGB</code> describing the desired red, green and blue values.
   *
   * <p>Note: it is considered an error to attempt to dispose of colors
   * that were created by this method. An <code>IllegalStateException</code> is
   * thrown in this case.</p>
   *
   * @param rgb the RGB values of the desired color - must not be null
   * @return the color
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the rgb argument is null</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   *
   * @see RGB
   * @see Device#getSystemColor
   */
  public static Color getColor( RGB rgb ) {
    checkThread();
    if( rgb == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return RWTFactory.getResourceFactory().getColor( rgb.red, rgb.green, rgb.blue );
  }

  /**
   * Returns a {@link Color} given the
   * desired red, green and blue values expressed as ints in the range
   * 0 to 255 (where 0 is black and 255 is full brightness).
   *
   * <p>Note: it is considered an error to attempt to dispose of colors
   * that were created by this method. An <code>IllegalStateException</code> is
   * thrown in this case.</p>
   *
   * @param red the amount of red in the color
   * @param green the amount of green in the color
   * @param blue the amount of blue in the color
   * @return the color
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   */
  public static Color getColor( int red, int green, int blue ) {
    checkThread();
    return RWTFactory.getResourceFactory().getColor( red, green, blue );
  }

  /**
   * Returns a new font given a font data
   * which describes the desired font's appearance.
   *
   * <p>Note: it is considered an error to attempt to dispose of fonts
   * that were created by this method. An <code>IllegalStateException</code> is
   * thrown in this case.</p>
   *
   * @param data the {@link FontData} to use - must not be null
   * @return the font
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the data argument is null</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   */
  public static Font getFont( FontData data ) {
    checkThread();
    if( data == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return RWTFactory.getResourceFactory().getFont( data );
  }

  /**
   * Returns a {@link Font} object given a font name, the height of the desired
   * font in points, and a font style.
   *
   * <p>Note: it is considered an error to attempt to dispose of fonts
   * that were created by this method. An <code>IllegalStateException</code> is
   * thrown in this case.</p>
   *
   * @param name the name of the font (must not be null)
   * @param height the font height in points
   * @param style a bit or combination of <code>NORMAL</code>,
   *          <code>BOLD</code>, <code>ITALIC</code>
   * @return the font
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the name argument is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the height is negative</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   */
  public static Font getFont( String name, int height, int style ) {
    checkThread();
    return RWTFactory.getResourceFactory().getFont( new FontData( name, height, style ) );
  }

  /**
   * Returns an instance of {@link Image} based on the specified image path. The
   * image has to be on the applications class-path.
   *
   * @param path the path to the image
   * @return the image
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_NULL_ARGUMENT - if the path is null</li>
   *              <li>ERROR_ILLEGAL_ARGUMENT - if the path is invalid</li>
   *              </ul>
   * @exception IllegalArgumentException <ul>
   *              <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
   *              current display's UI thread</li>
   *              </ul>
   * @exception SWTException <ul>
   *              <li>ERROR_IO - if an IO error occurs while reading from the
   *              stream</li>
   *              <li>ERROR_INVALID_IMAGE - if the image stream contains invalid
   *              data</li>
   *              <li>ERROR_UNSUPPORTED_FORMAT - if the image stream contains an
   *              unrecognized format</li>
   *              </ul>
   * @deprecated Use {@link #getImage(String, ClassLoader)} or
   *             {@link #getImage(String, InputStream)} instead.
   */
  public static Image getImage( String path ) {
    checkThread();
    if( path == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( "".equals( path ) ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return RWTFactory.getImageFactory().findImage( path );
  }

  /**
   * Returns an instance of {@link Image} based on the specified image path. The
   * image has to be on the applications class-path. Uses the specified
   * classloader to load the image.
   *
   * @param path the path to the image
   * @param imageLoader the classloader to use
   * @return the image
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the path is null</li>
   *    <li>ERROR_ILLEGAL_ARGUMENT - if the path is invalid</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_IO - if an IO error occurs while reading from the stream</li>
   *    <li>ERROR_INVALID_IMAGE - if the image stream contains invalid data</li>
   *    <li>ERROR_UNSUPPORTED_FORMAT - if the image stream contains an unrecognized format</li>
   * </ul>
   */
  public static Image getImage( String path, ClassLoader imageLoader ) {
    checkThread();
    if( path == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( "".equals( path ) ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return RWTFactory.getImageFactory().findImage( path, imageLoader );
  }

  /**
   * Returns an instance of {@link Image} based on the specified image path. The
   * image will be read from the provided InputStream.
   *
   * @param path the path the image resource is registered at
   * @param inputStream the input stream for the image
   * @return the image
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the path is null</li>
   *    <li>ERROR_NULL_ARGUMENT - if the inputStream is null</li>
   *    <li>ERROR_INVALID_ARGUMENT - if the path is invalid</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_IO - if an IO error occurs while reading from the stream</li>
   *    <li>ERROR_INVALID_IMAGE - if the image stream contains invalid data</li>
   *    <li>ERROR_UNSUPPORTED_FORMAT - if the image stream contains an unrecognized format</li>
   * </ul>
   */
  public static Image getImage( String path, InputStream inputStream ) {
    checkThread();
    if( path == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( inputStream == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( "".equals( path ) ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    return RWTFactory.getImageFactory().findImage( path, inputStream );
  }

  /**
   * Returns an instance of {@link Cursor} based on a style constant describing
   * the desired cursor appearance.
   *
   * <p>Note: it is considered an error to attempt to dispose of cursors
   * that were created by this method. An <code>IllegalStateException</code> is
   * thrown in this case.</p>
   *
   * @param style the style of the cursor to create
   * @return the cursor
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   *
   * @see SWT#CURSOR_ARROW
   * @see SWT#CURSOR_WAIT
   * @see SWT#CURSOR_CROSS
   * @see SWT#CURSOR_HELP
   * @see SWT#CURSOR_SIZEALL
   * @see SWT#CURSOR_SIZENS
   * @see SWT#CURSOR_SIZEWE
   * @see SWT#CURSOR_SIZEN
   * @see SWT#CURSOR_SIZES
   * @see SWT#CURSOR_SIZEE
   * @see SWT#CURSOR_SIZEW
   * @see SWT#CURSOR_SIZENE
   * @see SWT#CURSOR_SIZESE
   * @see SWT#CURSOR_SIZESW
   * @see SWT#CURSOR_SIZENW
   * @see SWT#CURSOR_IBEAM
   * @see SWT#CURSOR_HAND
   *
   * @since 1.2
   * @deprecated use {@link org.eclipse.swt.widgets.Display#getSystemCursor(int)
   * Display#getSystemCursor(int)}
   */
  public static Cursor getCursor( int style ) {
    checkThread();
    return RWTFactory.getResourceFactory().getCursor( style );
  }

  //////////////////////////
  // Text-Size-Determination

  /**
   * Returns the extent of the given string. Tab expansion and carriage return
   * processing are performed.
   * <p>
   * The <em>extent</em> of a string is the width and height of the
   * rectangular area it would cover if drawn in a particular font.
   * </p>
   *
   * @param font the font for which the result is valid
   * @param string the string to measure
   * @param wrapWidth the maximum width of the text. The text will be wrapped to
   *          match this width. If set to 0, no wrapping will be performed.
   * @return a point containing the extent of the string
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the font or string argument is null</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   */
  public static Point textExtent( Font font, String string, int wrapWidth ) {
    checkThread();
    if( font == null || string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return TextSizeUtil.textExtent( font, string, wrapWidth );
  }

  /**
   * Returns the extent of the given string. No tab expansion or carriage return
   * processing will be performed.
   * <p>
   * The <em>extent</em> of a string is the width and height of the
   * rectangular area it would cover if drawn in a particular font.
   * </p>
   *
   * @param font the font for which the result is valid
   * @param string the string to measure
   * @return a point containing the extent of the string
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the font or string arguemnt is null</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   */
  public static Point stringExtent( Font font, String string ) {
    checkThread();
    if( font == null || string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return TextSizeUtil.stringExtent( font, string );
  }

  /**
   * Returns the height of the specified font, measured in pixels.
   *
   * @param font the font for which the result is valid
   * @return the height of the font
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the font argument is null</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   */
  public static int getCharHeight( Font font ) {
    checkThread();
    if( font == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return TextSizeUtil.getCharHeight( font );
  }

  /**
   * Returns the average character width of the specified font, measured in
   * pixels.
   *
   * @param font the font for which the result is valid
   * @return the average character width of the font
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the font argument is null</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the current display's UI thread</li>
   * </ul>
   */
  public static float getAvgCharWidth( Font font ) {
    if( font == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return TextSizeUtil.getAvgCharWidth( font );
  }

  static void checkThread() {
    if( getDisplayThread() != Thread.currentThread () ) {
      SWT.error( SWT.ERROR_THREAD_INVALID_ACCESS );
    }
  }

  private static Thread getDisplayThread() {
    Thread result = null;
    Display display = LifeCycleUtil.getSessionDisplay();
    if( display != null ) {
      result = display.getThread();
    }
    return result;
  }

  private Graphics() {
    // prevent instantiation
  }
}
