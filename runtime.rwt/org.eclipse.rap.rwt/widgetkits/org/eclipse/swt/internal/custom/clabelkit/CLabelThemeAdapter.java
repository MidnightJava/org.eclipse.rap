/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.clabelkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;


public final class CLabelThemeAdapter extends ControlThemeAdapter {

  public int getBorderWidth( CLabel clabel ) {
    int result = super.getBorderWidth( clabel );
    if(    ( clabel.getStyle() & SWT.SHADOW_IN ) != 0
        || ( clabel.getStyle() & SWT.SHADOW_OUT ) != 0 )
    {
      result = 1;
    }
    return result;
  }

  public int getSpacing( CLabel clabel ) {
    return getCssDimension( "CLabel", "spacing", clabel );
  }
}
