/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.widgets.fileuploadkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.widgets.FileUpload;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Widget;


public final class FileUploadLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.FileUpload";
  private static final String[] ALLOWED_STYLES = new String[] { "BORDER" };

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";

  public void preserveValues( Widget widget ) {
    FileUpload fileUpload = ( FileUpload ) widget;
    ControlLCAUtil.preserveValues( fileUpload );
    WidgetLCAUtil.preserveCustomVariant( fileUpload );
    preserveProperty( fileUpload, PROP_TEXT, fileUpload.getText() );
    preserveProperty( fileUpload, PROP_IMAGE, fileUpload.getImage() );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    FileUpload fileUpload = ( FileUpload ) widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( fileUpload );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( fileUpload.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( fileUpload, ALLOWED_STYLES ) );
  }

  public void readData( Widget widget ) {
    FileUpload fileUpload = ( FileUpload ) widget;
    readFileName( fileUpload );
  }

  public void renderChanges( Widget widget ) throws IOException {
    FileUpload fileUpload = ( FileUpload ) widget;
    ControlLCAUtil.renderChanges( fileUpload );
    WidgetLCAUtil.renderCustomVariant( fileUpload );
    renderProperty( fileUpload, PROP_TEXT, fileUpload.getText(), "" );
    renderProperty( fileUpload, PROP_IMAGE, fileUpload.getImage(), null );
    renderSubmit( fileUpload );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  /////////
  // Helper

  private void readFileName( FileUpload fileUpload ) {
    IFileUploadAdapter adapter = fileUpload.getAdapter( IFileUploadAdapter.class );
    String fileName = WidgetLCAUtil.readPropertyValue( fileUpload, "fileName" );
    if( fileName != null ) {
      adapter.setFileName( fileName == "" ? null : fileName );
      SelectionEvent event = new SelectionEvent( fileUpload, null, SelectionEvent.WIDGET_SELECTED );
      event.processEvent();
    }
  }

  private static void renderSubmit( FileUpload fileUpload ) {
    String url = fileUpload.getAdapter( IFileUploadAdapter.class ).getAndResetUrl();
    if( url != null ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( fileUpload );
      Map<String, Object> args = new HashMap<String, Object>();
      args.put( "url", url );
      clientObject.call( "submit", args );
    }
  }

}
