/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tabfolderkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;


public class TabFolderLCA extends AbstractWidgetLCA {

  public void preserveValues( Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    WidgetLCAUtil.preserveCustomVariant( widget );
  }

  public void readData( Widget widget ) {
    ControlLCAUtil.processMouseEvents( ( TabFolder )widget );
    ControlLCAUtil.processKeyEvents( ( TabFolder )widget );
    ControlLCAUtil.processMenuDetect( ( TabFolder )widget );
    WidgetLCAUtil.processHelp( widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    TabFolder tabFolder = ( TabFolder )widget;
    JSWriter writer = JSWriter.getWriterFor( tabFolder );
    writer.newWidget( "qx.ui.pageview.tabview.TabView" );
    writer.set( "hideFocus", true );
    if( ( tabFolder.getStyle() & SWT.BOTTOM ) != 0 ) {
      writer.set( "placeBarOnTop", false );
    }
    ControlLCAUtil.writeStyleFlags( tabFolder );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ControlLCAUtil.writeChanges( ( Control )widget );
    WidgetLCAUtil.writeCustomVariant( widget );
  }

  public void renderDispose( Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public Rectangle adjustCoordinates( Widget widget, Rectangle newBounds ) {
    Control control = ( Control )widget;
    TabFolder tabFolder = ( TabFolder )control.getParent();
    boolean onBottom = ( tabFolder.getStyle() & SWT.BOTTOM ) != 0;
    int border = tabFolder.getBorderWidth() + 1;
    int hTabBar = onBottom ? 0 : 23;
    return new Rectangle( newBounds.x - border - 10,
                          newBounds.y - hTabBar - border - 10,
                          newBounds.width,
                          newBounds.height );
  }
}
