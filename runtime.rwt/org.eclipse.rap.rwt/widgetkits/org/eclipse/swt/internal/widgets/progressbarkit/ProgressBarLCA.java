/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.progressbarkit;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;


public class ProgressBarLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.ProgressBar";
  private static final String[] ALLOWED_STYLES = new String[] {
    "SMOOTH", "HORIZONTAL", "VERTICAL", "INDETERMINATE", "BORDER"
  };

  static final String PROP_MINIMUM = "minimum";
  static final String PROP_MAXIMUM = "maximum";
  static final String PROP_SELECTION = "selection";
  static final String PROP_STATE = "state";

  // Default values
  private static final Integer DEFAULT_MINIMUM = new Integer( 0 );
  private static final Integer DEFAULT_MAXIMUM = new Integer( 100 );
  private static final Integer DEFAULT_SELECTION = new Integer( 0 );
  private static final String DEFAULT_STATE = "normal";

  public void preserveValues( Widget widget ) {
    ProgressBar progressBar = ( ProgressBar )widget;
    ControlLCAUtil.preserveValues( progressBar );
    WidgetLCAUtil.preserveCustomVariant( progressBar );
    preserveProperty( progressBar, PROP_MINIMUM, new Integer( progressBar.getMinimum() ) );
    preserveProperty( progressBar, PROP_MAXIMUM, new Integer( progressBar.getMaximum() ) );
    preserveProperty( progressBar, PROP_SELECTION, new Integer( progressBar.getSelection() ) );
    preserveProperty( progressBar, PROP_STATE, getState( progressBar ) );
  }

  public void readData( Widget widget ) {
    ProgressBar progressBar = ( ProgressBar )widget;
    ControlLCAUtil.processMouseEvents( progressBar );
    ControlLCAUtil.processKeyEvents( progressBar );
    ControlLCAUtil.processMenuDetect( progressBar );
    WidgetLCAUtil.processHelp( progressBar );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    ProgressBar progressBar = ( ProgressBar )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( progressBar );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( progressBar.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( progressBar, ALLOWED_STYLES ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    ProgressBar pBar = ( ProgressBar )widget;
    ControlLCAUtil.renderChanges( pBar );
    WidgetLCAUtil.renderCustomVariant( pBar );
    renderProperty( pBar, PROP_MINIMUM, new Integer( pBar.getMinimum() ), DEFAULT_MINIMUM );
    renderProperty( pBar, PROP_MAXIMUM, new Integer( pBar.getMaximum() ), DEFAULT_MAXIMUM );
    renderProperty( pBar, PROP_SELECTION, new Integer( pBar.getSelection() ), DEFAULT_SELECTION );
    renderProperty( pBar, PROP_STATE, getState( pBar ), DEFAULT_STATE );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  //////////////////
  // Helping methods

  private static String getState( ProgressBar progressBar ) {
    String result = "normal";
    int state = progressBar.getState();
    if( state == SWT.ERROR ) {
      result = "error";
    } else if( state == SWT.PAUSED ) {
      result = "paused";
    }
    return result;
  }
}
