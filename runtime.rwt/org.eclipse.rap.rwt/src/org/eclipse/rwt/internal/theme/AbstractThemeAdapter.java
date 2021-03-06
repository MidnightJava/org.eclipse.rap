/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Widget;


/**
 * Base class for theme adapters.
 */
public abstract class AbstractThemeAdapter implements IThemeAdapter {

  private final WidgetMatcher matcher;

  public AbstractThemeAdapter() {
    matcher = new WidgetMatcher();
    configureMatcher( matcher );
  }

  /**
   * Returns the name of the main CSS element for a given widget.
   */
  public static String getPrimaryElement( Widget widget ) {
    Class widgetClass = widget.getClass();
    ThemeableWidget thWidget = findThemeableWidget( widget );
    if( thWidget == null || thWidget.elements == null ) {
      throw new RuntimeException( "No themeable widget found for " + widgetClass.getName() );
    }
    return thWidget.elements[ 0 ].getName();
  }

  /**
   * Configures the widget matcher to be able to match widgets. Subclasses need
   * to implement.
   */
  protected abstract void configureMatcher( WidgetMatcher matcher );

  ////////////////////
  // Delegator methods

  protected Color getCssColor( String cssElement, String cssProperty, Widget widget ) {
    QxType cssValue = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return QxColor.createColor( ( QxColor )cssValue );
  }

  protected Font getCssFont( String cssElement, String cssProperty, Widget widget ) {
    QxType cssValue = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return QxFont.createFont( ( QxFont )cssValue );
  }

  protected int getCssBorderWidth( String cssElement, String cssProperty, Widget widget ) {
    QxType cssValue = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return ( ( QxBorder )cssValue ).width;
  }

  protected int getCssDimension( String cssElement, String cssProperty, Widget widget ) {
    QxType cssValue = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return ( ( QxDimension )cssValue ).value;
  }

  protected Rectangle getCssBoxDimensions( String cssElement, String cssProperty, Widget widget ) {
    QxType cssValue = ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return QxBoxDimensions.createRectangle( ( QxBoxDimensions )cssValue );
  }

  protected Point getCssImageDimension( String cssElement, String cssProperty, Widget widget ) {
    QxImage image = ( QxImage ) ThemeUtil.getCssValue( cssElement, cssProperty, matcher, widget );
    return new Point( image.width, image.height );
  }

  private static ThemeableWidget findThemeableWidget( Widget widget ) {
    ThemeableWidget result;
    Class widgetClass = widget.getClass();
    ThemeManager manager = RWTFactory.getThemeManager();
    result = manager.getThemeableWidget( widgetClass );
    while( ( result == null || result.elements == null ) && widgetClass.getSuperclass() != null ) {
      widgetClass = widgetClass.getSuperclass();
      result = manager.getThemeableWidget( widgetClass );
    }
    return result;
  }
}
