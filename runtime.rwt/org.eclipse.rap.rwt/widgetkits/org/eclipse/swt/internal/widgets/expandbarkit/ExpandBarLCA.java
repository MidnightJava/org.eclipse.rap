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
package org.eclipse.swt.internal.widgets.expandbarkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IExpandBarAdapter;
import org.eclipse.swt.widgets.*;


public final class ExpandBarLCA extends AbstractWidgetLCA {

  // Property names for preserveValues
  public static final String PROP_BOTTOM_SPACING_BOUNDS = "bottomSpacingBounds";
  public static final String PROP_SHOW_VSCROLLBAR = "showVScrollbar";
  public static final String PROP_VSCROLLBAR_MAX = "vScrollbarMax";

  public void preserveValues( Widget widget ) {
    ExpandBar expandBar = ( ExpandBar )widget;
    ControlLCAUtil.preserveValues( expandBar );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( expandBar );
    adapter.preserve( PROP_SHOW_VSCROLLBAR,
                      Boolean.valueOf( expandBarAdapter.isVScrollbarVisible() ) );
    adapter.preserve( PROP_VSCROLLBAR_MAX, getVScrollbarMax( expandBar ) );
    adapter.preserve( PROP_BOTTOM_SPACING_BOUNDS, expandBarAdapter.getBottomSpacingBounds() );
  }

  public void readData( Widget widget ) {
    ControlLCAUtil.processKeyEvents( ( Control )widget );
    ControlLCAUtil.processMenuDetect( ( Control )widget );
    WidgetLCAUtil.processHelp( widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    ExpandBar expandBar = ( ExpandBar )widget;
    JSWriter writer = JSWriter.getWriterFor( expandBar );
    writer.newWidget( "org.eclipse.swt.widgets.ExpandBar" );
    WidgetLCAUtil.writeCustomVariant( widget );
    ControlLCAUtil.writeStyleFlags( expandBar );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ExpandBar expandBar = ( ExpandBar )widget;
    ControlLCAUtil.writeChanges( expandBar );
    writeBottomSpacing( expandBar );
    writeShowVScrollbar( expandBar );
    writeVScrollbarMax( expandBar );
  }

  public void renderDispose( Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public static IExpandBarAdapter getExpandBarAdapter( ExpandBar bar ) {
    return bar.getAdapter( IExpandBarAdapter.class );
  }

  //////////////////////////////////////
  // Helping methods to write properties
  private static void writeBottomSpacing( ExpandBar bar )
    throws IOException
  {
    IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( bar );
    Rectangle bottomSpacingBounds = expandBarAdapter.getBottomSpacingBounds();
    if( WidgetLCAUtil.hasChanged( bar, PROP_BOTTOM_SPACING_BOUNDS, bottomSpacingBounds ) ) {
      JSWriter writer = JSWriter.getWriterFor( bar );
      writer.call( "setBottomSpacingBounds", new Object[]{
        new Integer( bottomSpacingBounds.x ),
        new Integer( bottomSpacingBounds.y ),
        new Integer( bottomSpacingBounds.width ),
        new Integer( bottomSpacingBounds.height )
      } );
    }
  }

  private static void writeShowVScrollbar( ExpandBar bar ) throws IOException {
    IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( bar );
    Boolean newValue = Boolean.valueOf( expandBarAdapter.isVScrollbarVisible() );
    if( WidgetLCAUtil.hasChanged( bar, PROP_SHOW_VSCROLLBAR, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( bar );
      writer.call( "showVScrollbar", new Object[] { newValue } );
    }
  }

  private static void writeVScrollbarMax( ExpandBar bar ) throws IOException {
    Integer newValue = getVScrollbarMax( bar );
    if( WidgetLCAUtil.hasChanged( bar, PROP_VSCROLLBAR_MAX, newValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( bar );
      writer.call( "setVScrollbarMax", new Object[] { newValue } );
    }
  }

  private static Integer getVScrollbarMax( ExpandBar bar ) {
    int result = 0;
    IExpandBarAdapter expandBarAdapter = getExpandBarAdapter( bar );
    ExpandItem[] items = bar.getItems();
    for( int i = 0; i < items.length; i++ ) {
      result += expandBarAdapter.getBounds( items[ i ] ).height;
    }
    result += bar.getSpacing() * ( items.length + 1 );
    return new Integer( result );
  }
}
