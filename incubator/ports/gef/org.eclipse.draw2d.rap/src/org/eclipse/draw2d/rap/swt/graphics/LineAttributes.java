/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.rap.swt.graphics;

import org.eclipse.swt.*;

/**
 * <code>LineAttributes</code> defines a set of line attributes that
 * can be modified in a GC.
 * <p>
 * Application code does <em>not</em> need to explicitly release the
 * resources managed by each instance when those instances are no longer
 * required, and thus no <code>dispose()</code> method is provided.
 * </p>
 *
 * @see GC#getLineAttributes()
 * @see GC#setLineAttributes(LineAttributes)
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 *
 * @since 1.3
 */
public class LineAttributes extends org.eclipse.swt.graphics.LineAttributes {

	/**
   * The line style.
   * 
   * @see org.eclipse.swt.SWT#LINE_CUSTOM
   * @see org.eclipse.swt.SWT#LINE_DASH
   * @see org.eclipse.swt.SWT#LINE_DASHDOT
   * @see org.eclipse.swt.SWT#LINE_DASHDOTDOT
   * @see org.eclipse.swt.SWT#LINE_DOT
   * @see org.eclipse.swt.SWT#LINE_SOLID
   */
  public int style;

	/**
   * The line dash style for SWT.LINE_CUSTOM.
   */
  public float[] dash;
  
  /**
   * The line dash style offset for SWT.LINE_CUSTOM.
   */
  public float dashOffset;

  /**
   * The line miter limit.
   */
  public float miterLimit;

  /**
   * Create a new line attributes with the specified line width.
   *
   * @param width the line width
   */
  public LineAttributes( final float width ) {
  	super( width );
  }

  /**
   * Create a new line attributes with the specified line cap, join and width.
   *
   * @param width the line width
   * @param cap the line cap style
   * @param join the line join style
   */
  public LineAttributes( final float width, final int cap, final int join ) {
    super(width,cap,join);
  }

}
