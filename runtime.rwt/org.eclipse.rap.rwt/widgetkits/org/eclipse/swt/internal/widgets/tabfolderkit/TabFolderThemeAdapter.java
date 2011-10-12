/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tabfolderkit;

import org.eclipse.rwt.internal.theme.QxBoxDimensions;
import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.SimpleSelector;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapter;
import org.eclipse.swt.widgets.TabFolder;

public final class TabFolderThemeAdapter extends ControlThemeAdapter {

  public int getContentContainerBorderWidth( TabFolder folder ) {
    return getCssBorderWidth( "TabFolder-ContentContainer", "border", folder );
  }

  public Rectangle getItemPadding( boolean selected ) {
    SimpleSelector selector = selected ? SimpleSelector.SELECTED : SimpleSelector.DEFAULT;
    QxType cssValue = ThemeUtil.getCssValue( "TabItem", "padding", selector );
    return QxBoxDimensions.createRectangle( ( QxBoxDimensions )cssValue );
  }
}
