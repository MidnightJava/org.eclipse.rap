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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rwt.internal.widgets.IFileUploadAdapter;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("deprecation")
public class FileUploadLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private FileUpload fileUpload;
  private FileUploadLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    fileUpload = new FileUpload( shell, SWT.NONE );
    lca = new FileUploadLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( fileUpload );
    ControlLCATestUtil.testFocusListener( fileUpload );
    ControlLCATestUtil.testMouseListener( fileUpload );
    ControlLCATestUtil.testKeyListener( fileUpload );
    ControlLCATestUtil.testTraverseListener( fileUpload );
    ControlLCATestUtil.testMenuDetectListener( fileUpload );
    ControlLCATestUtil.testHelpListener( fileUpload );
  }

  public void testPreserveBounds() {
    Fixture.markInitialized( display );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( fileUpload );
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    fileUpload.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( fileUpload );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
  }

  public void testReadFileName() {
    String uploadId = WidgetUtil.getId( fileUpload );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( uploadId + ".fileName", "foo" );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "foo", fileUpload.getFileName() );
    Fixture.fakeNewRequest( display );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "foo", fileUpload.getFileName() );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( uploadId + ".fileName", "" );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( null, fileUpload.getFileName() );
  }

  public void testFireSelectionEvent() {
    final List<SelectionEvent> eventLog = new ArrayList<SelectionEvent>();
    fileUpload.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        eventLog.add( event );
      }
    } );
    String uploadId = WidgetUtil.getId( fileUpload );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( uploadId + ".fileName", "foo" );
    Fixture.executeLifeCycleFromServerThread( );
    assertEquals( "foo", fileUpload.getFileName() );
    assertEquals( 1, eventLog.size() );
    SelectionEvent event = eventLog.get( 0 );
    assertSame( fileUpload, event.widget );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( fileUpload );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertEquals( "rwt.widgets.FileUpload", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( fileUpload );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertEquals( WidgetUtil.getId( fileUpload.getParent() ), operation.getParent() );
  }

  public void testRenderDispose() throws IOException {
    lca.renderDispose( fileUpload );

    Message message = Fixture.getProtocolMessage();
    Operation operation = message.getOperation( 0 );
    assertTrue( operation instanceof DestroyOperation );
    assertEquals( WidgetUtil.getId( fileUpload ), operation.getTarget() );
  }

  public void testRenderInitialText() throws IOException {
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "text" ) );
  }

  public void testRenderText() throws IOException {
    fileUpload.setText( "test" );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( fileUpload, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );

    fileUpload.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "text" ) );
  }
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    fileUpload.setImage( image );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( fileUpload, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    fileUpload.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    fileUpload.setImage( image );

    Fixture.preserveWidgets();
    fileUpload.setImage( null );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( fileUpload, "image" ) );
  }

  public void testSubmit() throws IOException {
    IFileUploadAdapter adapter = getFileUploadAdapter( fileUpload );
    adapter.setFileName( "foo" );

    fileUpload.submit( "bar" );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = message.findCallOperation( fileUpload, "submit" );
    assertEquals( "bar", operation.getProperty( "url" ) );
  }

  public void testSubmitWithoutFileName() throws IOException {
    fileUpload.submit( "bar" );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCallOperation( fileUpload, "submit" ) );
  }

  public void testRenderInitialCustomVariant() throws IOException {
    lca.render( fileUpload );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( fileUpload );
    assertTrue( operation.getPropertyNames().indexOf( "customVariant" ) == -1 );
  }

  public void testRenderCustomVariant() throws IOException {
    fileUpload.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "variant_blue", message.findSetProperty( fileUpload, "customVariant" ) );
  }

  public void testRenderCustomVariantUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( fileUpload );

    fileUpload.setData( WidgetUtil.CUSTOM_VARIANT, "blue" );
    Fixture.preserveWidgets();
    lca.renderChanges( fileUpload );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( fileUpload, "customVariant" ) );
  }

  private IFileUploadAdapter getFileUploadAdapter( FileUpload upload ) {
    return upload.getAdapter( IFileUploadAdapter.class );
  }
}
