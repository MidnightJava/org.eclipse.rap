/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.internal.widgets.controldecoratorkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.ControlDecorator;
import org.eclipse.swt.widgets.Widget;


public class ControlDecoratorLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ControlDecorator";
  private static final String[] ALLOWED_STYLES = new String[] {
    "TOP", "BOTTOM", "LEFT", "RIGHT", "CENTER"
  };

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_VISIBLE = "visible";
  private static final String PROP_SHOW_HOVER = "showHover";
  private static final String PROP_SELECTION_LISTENER = "selection";

  public void preserveValues( Widget widget ) {
    ControlDecorator decorator = ( ControlDecorator )widget;
    WidgetLCAUtil.preserveBounds( decorator, decorator.getBounds() );
    preserveProperty( decorator, PROP_TEXT, decorator.getText() );
    preserveProperty( decorator, PROP_IMAGE, decorator.getImage() );
    preserveProperty( decorator, PROP_VISIBLE, decorator.isVisible() );
    preserveProperty( decorator, PROP_SHOW_HOVER, decorator.getShowHover() );
    preserveListener( decorator, PROP_SELECTION_LISTENER, SelectionEvent.hasListener( decorator ) );
  }

  public void readData( Widget widget ) {
    readSelectionEvent( ( ControlDecorator )widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    ControlDecorator decorator = ( ControlDecorator )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( decorator );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( decorator.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( decorator, ALLOWED_STYLES ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ControlDecorator decorator = ( ControlDecorator )widget;
    WidgetLCAUtil.renderBounds( decorator, decorator.getBounds() );
    renderProperty( decorator, PROP_TEXT, decorator.getText(), "" );
    renderProperty( decorator, PROP_IMAGE, decorator.getImage(), null );
    renderProperty( decorator, PROP_VISIBLE, decorator.isVisible(), false );
    renderProperty( decorator, PROP_SHOW_HOVER, decorator.getShowHover(), true );
    renderListener( decorator,
                    PROP_SELECTION_LISTENER,
                    SelectionEvent.hasListener( decorator ),
                    false );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  ////////////////////////////////////////////////////
  // Helping methods to read client-side state changes

  private static void readSelectionEvent( ControlDecorator decorator ) {
    if( WidgetLCAUtil.wasEventSent( decorator, JSConst.EVENT_WIDGET_SELECTED ) ) {
      processSelectionEvent( decorator, SelectionEvent.WIDGET_SELECTED );
    }
    if( WidgetLCAUtil.wasEventSent( decorator, JSConst.EVENT_WIDGET_DEFAULT_SELECTED ) ) {
      processSelectionEvent( decorator, SelectionEvent.WIDGET_DEFAULT_SELECTED );
    }
  }

  private static void processSelectionEvent( ControlDecorator decorator, int id ) {
    Rectangle bounds = new Rectangle( 0, 0, 0, 0 );
    int stateMask = EventLCAUtil.readStateMask( JSConst.EVENT_WIDGET_SELECTED_MODIFIER );
    SelectionEvent event = new SelectionEvent( decorator,
                                               null,
                                               id,
                                               bounds,
                                               stateMask,
                                               "",
                                               true,
                                               SWT.NONE );
    event.processEvent();
  }
}
