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
package org.eclipse.rwt.internal.branding;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.branding.Header;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.service.StartupPageTemplateHolder;


public class BrandingUtil_Test extends TestCase {

  public void testReplacePlaceholder() {
    String templateString = StartupPageTemplateHolder.VAR_ENTRY_POINT.toString();
    StartupPageTemplateHolder template = new StartupPageTemplateHolder( templateString );

    BrandingUtil.replacePlaceholder( template,
                                     StartupPageTemplateHolder.VAR_ENTRY_POINT,
                                     "replacement" );
    assertEquals( "replacement", getTemplateContent( template ).toString() );

    template.reset();
    BrandingUtil.replacePlaceholder( template,
                                     StartupPageTemplateHolder.VAR_ENTRY_POINT,
                                     null );
    assertEquals( "", getTemplateContent( template ).toString() );
  }

  private StringBuilder getTemplateContent( final StartupPageTemplateHolder template ) {
    String[] tokens = template.getTokens();
    StringBuilder result = new StringBuilder();
    for( int i = 0; i < tokens.length; i++ ) {
      result.append( tokens[ i ] );
    }
    return result;
  }

  public void testHeaderMarkup() {
    String expected;
    String markup;
    TestBranding branding = new TestBranding();
    // No fav icon
    branding.favIcon = null;
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "", markup );
    branding.favIcon = "";
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "", markup );
    // Ordinary fav icon
    branding.favIcon = "my/fav/icon.ico";
    markup = BrandingUtil.headerMarkup( branding );
    expected
      = "<link rel=\"shortcut icon\" "
      + "type=\"image/x-icon\" "
      + "href=\"rwt-resources/my/fav/icon.ico\" />\n";
    assertEquals( expected, markup );
    // Some header without attributes
    branding.favIcon = null;
    branding.headers = new Header[] {
      new Header( "meta", new String[ 0 ], new String[ 0 ] )
    };
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "<meta />\n", markup );
    // Some header with attributes
    branding.favIcon = null;
    branding.headers = new Header[] {
      new Header( "meta", new String[] { "name" }, new String[] { "value" } )
    };
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "<meta name=\"value\" />\n", markup );
    // Header with attributes that have a null-name or-value: will be ignored
    branding.favIcon = null;
    branding.headers = new Header[] {
      new Header( "meta",
                  new String[] { null, "name", null },
                  new String[] { null, null, "value" } )
    };
    markup = BrandingUtil.headerMarkup( branding );
    assertEquals( "<meta />\n", markup );
    // fav icon and header
    branding.favIcon = "my/fav/icon.ico";
    branding.headers = new Header[] {
      new Header( "meta", new String[] { "name" }, new String[] { "value" } )
    };
    markup = BrandingUtil.headerMarkup( branding );
    expected
      = "<link href=\"my/fav/icon.ico\" "
      + "type=\"image/x-icon\" "
      + "rel=\"shortcut icon\" />\n"
      + "<meta name=\"value\" />\n";
  }

  public void testGetCurrentBrandingId1() {
    BrandingUtil.determineBranding();
    String currentBrandingId =  BrandingUtil.getCurrentBrandingId();
    assertEquals( BrandingManager.DEFAULT_BRANDING_ID, currentBrandingId );
  }

  public void testGetCurrentBrandingId2() {
    TestBranding branding = new TestBranding();
    branding.servletName = "rap";
    RWTFactory.getBrandingManager().register( branding );
    try {
      BrandingUtil.determineBranding();
      String currentBrandingId =  BrandingUtil.getCurrentBrandingId();
      assertEquals( branding.getId(), currentBrandingId );
    } finally {
      RWTFactory.getBrandingManager().deregister( branding );
    }
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
