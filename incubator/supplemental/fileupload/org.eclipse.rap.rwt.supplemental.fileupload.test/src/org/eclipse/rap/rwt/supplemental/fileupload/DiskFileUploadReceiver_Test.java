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
package org.eclipse.rap.rwt.supplemental.fileupload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.supplemental.fileupload.internal.FileUploadDetails;
import org.eclipse.rap.rwt.supplemental.fileupload.test.FileUploadTestUtil;


public class DiskFileUploadReceiver_Test extends TestCase {

  private File createdFile;
  private File createdContentTypeFile;

  protected void tearDown() throws Exception {
    if( createdFile != null ) {
      createdFile.delete();
      createdFile = null;
    }
    if ( createdContentTypeFile != null ) {
      createdContentTypeFile.delete();
      createdContentTypeFile = null;
    }
  }

  public void testInitialGetTargetFile() {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();

    assertNull( receiver.getTargetFile() );
  }

  public void testCreateTargetFile() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();

    IFileUploadDetails details = new FileUploadDetails( "foo.bar", "text/plain", 5 );
    createdFile = receiver.createTargetFile( details );

    assertTrue( createdFile.exists() );
    assertEquals( "foo.bar", createdFile.getName() );
  }
  
  public void testCreateContentTypeFile() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();

    IFileUploadDetails details = new FileUploadDetails( "foo.bar", "text/plain", 5 );
    createdFile = receiver.createTargetFile( details );
    createdContentTypeFile = receiver.createContentTypeFile( createdFile, details );

    assertTrue( createdContentTypeFile.exists() );
  }
  
  public void testGetContentType() throws IOException {
    testReceive();
    assertEquals( "text/plain", DiskFileUploadReceiver.getContentType( createdFile ) );
    assertEquals( null, DiskFileUploadReceiver.getContentType( new File( "test" ) ) );
  }

  public void testCreatedTargetFilesDiffer() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();

    IFileUploadDetails details = new FileUploadDetails( "foo.bar", "text/plain", 5 );
    createdFile = receiver.createTargetFile( details );
    File createdFile2 = receiver.createTargetFile( details );
    createdFile2.deleteOnExit();

    assertFalse( createdFile.getAbsolutePath().equals( createdFile2.getAbsolutePath() ) );
  }

  public void testReceive() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();
    String content = "Hello world!";

    IFileUploadDetails details = new FileUploadDetails( "foo.bar", "text/plain", content.length() );
    receiver.receive( new ByteArrayInputStream( content.getBytes() ), details );
    createdFile = receiver.getTargetFile();

    assertNotNull( createdFile );
    assertTrue( createdFile.exists() );
    assertEquals( content, FileUploadTestUtil.getFileContents( createdFile ) );
  }

  public void testReceiveWithNullDetails() throws IOException {
    DiskFileUploadReceiver receiver = new DiskFileUploadReceiver();
    String content = "Hello world!";

    receiver.receive( new ByteArrayInputStream( content.getBytes() ), null );
    createdFile = receiver.getTargetFile();

    assertNotNull( createdFile );
    assertTrue( createdFile.exists() );
    assertEquals( "upload.tmp", createdFile.getName() );
    assertEquals( content, FileUploadTestUtil.getFileContents( createdFile ) );
  }
}
