/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


public class ThemeUtil_Test extends TestCase {
  private static final String CUSTOM_THEME_ID = "customThemeId";

  public void testCurrentThemeChanges() throws Exception {
    checkDefaultTheme();
    registerTheme();
    checkDefaultTheme();
    checkUnknownTheme();
    ThemeUtil.setCurrentThemeId( CUSTOM_THEME_ID );
    checkCurrentTheme();
    checkCssValue();
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private Theme createTheme( String themeId ) throws IOException {
    StyleSheet styleSheet = ThemeTestUtil.getStyleSheet( "TestExample.css" );
    return new Theme( themeId, "Custom Theme", styleSheet );
  }

  private void checkCssValue() {
    SimpleSelector selector = new SimpleSelector( new String[] { ".special" } );
    QxType cssValue = ThemeUtil.getCssValue( "Button", "color", selector );
    assertEquals( "#ff0000", ( ( QxColor )cssValue ).toDefaultString() );
  }

  private void checkCurrentTheme() {
    assertEquals( CUSTOM_THEME_ID, ThemeUtil.getCurrentThemeId() );
    assertNotSame( ThemeUtil.getDefaultTheme(), ThemeUtil.getTheme() );
    assertSame( RWTFactory.getThemeManager().getTheme( CUSTOM_THEME_ID ), ThemeUtil.getTheme() );
  }

  private void registerTheme() throws IOException {
    ThemeManagerHelper.resetThemeManager();
    ThemeManager manager = RWTFactory.getThemeManager();
    manager.registerTheme( createTheme( CUSTOM_THEME_ID ) );
    manager.activate();
  }

  private void checkUnknownTheme() {
    try {
      ThemeUtil.setCurrentThemeId( "woo.doo.schick.schnack" );
      fail( "should throw IAE for invalid theme ids" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  private void checkDefaultTheme() {
    assertNotNull( ThemeUtil.getDefaultTheme() );
    assertNotNull( ThemeUtil.getTheme() );
    assertSame( ThemeUtil.getDefaultTheme(), ThemeUtil.getTheme() );
  }
}