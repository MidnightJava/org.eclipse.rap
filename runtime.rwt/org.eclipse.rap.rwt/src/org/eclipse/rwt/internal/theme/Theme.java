/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.text.MessageFormat;

import org.eclipse.rwt.internal.theme.css.StyleSheet;


public class Theme {

  private static final String JS_THEME_PREFIX = "org.eclipse.swt.theme.";

  private final String id;
  private final String jsId;
  private final String name;
  private ThemeCssValuesMap valuesMap;
  private StyleSheetBuilder styleSheetBuilder;

  public Theme( String id, String name, StyleSheet styleSheet ) {
    if( id == null ) {
      throw new NullPointerException( "id" );
    }
    this.id = id;
    this.name = name != null ? name : "Unnamed Theme";
    jsId = createUniqueJsId( id );
    valuesMap = null;
    styleSheetBuilder = new StyleSheetBuilder();
    if( styleSheet != null ) {
      styleSheetBuilder.addStyleSheet( styleSheet );
    }
  }

  public String getId() {
    return id;
  }

  public String getJsId() {
    return jsId;
  }

  public String getName() {
    return name;
  }

  public void addStyleSheet( StyleSheet styleSheet ) {
    if( valuesMap != null ) {
      throw new IllegalStateException( "Theme is already initialized" );
    }
    styleSheetBuilder.addStyleSheet( styleSheet );
  }

  public void initialize( ThemeableWidget[] themeableWidgets ) {
    if( valuesMap != null ) {
      String pattern = "Theme ''{0}'' is already initialized.";
      String msg = MessageFormat.format( pattern, new Object[] { id } );
      throw new IllegalStateException( msg );
    }
    StyleSheet styleSheet = styleSheetBuilder.getStyleSheet();
    valuesMap = new ThemeCssValuesMap( styleSheet, themeableWidgets );
    styleSheetBuilder = null;
  }

  public ThemeCssValuesMap getValuesMap() {
    if( valuesMap == null ) {
      throw new IllegalStateException( "Theme is not initialized" );
    }
    return valuesMap;
  }

  private static String createUniqueJsId( String id ) {
    String result;
    if( ThemeManager.DEFAULT_THEME_ID.equals( id ) ) {
      result = JS_THEME_PREFIX + "Default";
    } else {
      String hash = Integer.toHexString( id.hashCode() );
      result = JS_THEME_PREFIX + "Custom_" + hash;
    }
    return result;
  }
}
