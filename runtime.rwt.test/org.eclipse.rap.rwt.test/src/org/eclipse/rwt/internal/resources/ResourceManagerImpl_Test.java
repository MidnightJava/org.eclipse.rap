/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.*;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


@SuppressWarnings("deprecation")
public class ResourceManagerImpl_Test extends TestCase {

  private final static String TEST_RESOURCE_1_JAR
    = "resources/js/resourcetest.js";
  private final static String TEST_RESOURCE_1
    = "org/eclipse/rwt/internal/resources/resourcetest1.js";
  private final static String TEST_RESOURCE_1_VERSIONED
    = "org/eclipse/rwt/internal/resources/resourcetest1-70fc4c0e.js";
  private final static String TEST_RESOURCE_2
    = "org/eclipse/rwt/internal/resources/resourcetest2.gif";
  private final static String TEST_RESOURCE_3
    = "org/eclipse/rwt/internal/resources/resourcetest3.gif";
  private final static String ISO_RESOURCE
    = "org/eclipse/rwt/internal/resources/iso-resource.js";
  private final static String UTF_8_RESOURCE
    = "org/eclipse/rwt/internal/resources/utf-8-resource.js";
  private static final String TEST_LOCATION_DISK
    = "rwt-resources/"
    + TEST_RESOURCE_1;
  private static final String TEST_LOCATION_VERSIONED_DISK
    = "rwt-resources/"
    + TEST_RESOURCE_1_VERSIONED;

  private static class CloseableInputStream extends ByteArrayInputStream {

    boolean closed;

    public CloseableInputStream() {
      super( new byte[ 1 ] );
    }

    public void close() throws IOException {
      closed = true;
      super.close();
    }

    boolean isClosed() {
      return closed;
    }
  }

  public void testInstanceCreation() {
    IResourceManager manager = getResourceManager();

    assertNotNull( "ResourceManager instance was not created", manager );
  }

  public void testRegistrationWithNotExistingResource() {
    IResourceManager manager = getResourceManager( );
    String doesNotExist = "doesNotExist";

    try {
      manager.register( doesNotExist );
      fail();
    } catch( ResourceRegistrationException expected ) {
    }
    assertFalse( manager.isRegistered( doesNotExist ) );
  }

  public void testRegistration() throws Exception {
    IResourceManager manager = getResourceManager( );
    String resource = TEST_RESOURCE_1_JAR;

    manager.register( resource );

    File jarFile = getResourceCopyFile( resource );
    assertTrue( "Resource not registered",  manager.isRegistered( resource ) );
    assertTrue( "Resource was not written to disk", jarFile.exists() );
    assertEquals( read( openStream( resource ) ), read( jarFile ) );
  }

  public void testRegistrationIsIdempotent() {
    IResourceManager manager = getResourceManager( );
    String resource = TEST_RESOURCE_1_JAR;
    manager.register( resource );
    clearTempFile();
    File jarFile = getResourceCopyFile( resource );

    manager.register( resource );

    assertFalse( "Resource must not be written twice", jarFile.exists() );
  }

  public void testRegistrationWithNullParams() {
    IResourceManager manager = getResourceManager( );
    try {
      manager.register( null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      String notAssigned = null;
      manager.register( "some-resource", notAssigned );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      manager.register( null, "UTF-8" );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
    try {
      manager.register( "some-resource", "UTF-8", null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException e ) {
      // expected
    }
  }

  public void testVersionedRegistrationWithNotExistingResource() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getResourceManager( );
    String doesNotExist = "doesNotExist";

    try {
      manager.register( doesNotExist, HTTP.CHARSET_UTF_8, RegisterOptions.NONE );
      fail();
    } catch( ResourceRegistrationException expected ) {
    }
    assertFalse( manager.isRegistered( doesNotExist ) );
  }

  public void testVersionedRegistration() throws Exception {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    ResourceManagerImpl manager = getResourceManager( );
    String resource = TEST_RESOURCE_1;

    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );

    File resourceFile = getResourceCopyFile( TEST_RESOURCE_1_VERSIONED );
    assertTrue( "Resource not registered", manager.isRegistered( resource ) );
    assertNotNull( "Versioned resource must have version number",
                   manager.findVersion( resource ) );
    assertTrue( "Resource was not written to disk", resourceFile.exists() );
    assertEquals( read( openStream( resource ) ), read( resourceFile ) );
  }

  public void testVersionedRegistrationIsIdempotent() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getResourceManager( );
    String resource = TEST_RESOURCE_1;
    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );
    clearTempFile();

    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );

    File resourceFile = getResourceCopyFile( TEST_RESOURCE_1_VERSIONED );
    assertFalse( "Resource must not be written twice", resourceFile.exists() );
  }

  public void testCompressedRegistration() throws Exception {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "false" );
    IResourceManager manager = getResourceManager( );
    String resource = TEST_RESOURCE_1;

    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.COMPRESS );

    File resourceFile = getResourceCopyFile( resource );
    byte[] origin = read( openStream( resource ) );
    byte[] copy = read( resourceFile );
    assertTrue( "Resource not registered", manager.isRegistered( resource ) );
    assertTrue( "Resource was not written to disk", resourceFile.exists() );
    assertTrue( "Compressed resource too big", origin.length > copy.length );
  }

  public void testCompressedRegistrationIsIdempotent() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "false" );
    IResourceManager manager = getResourceManager( );
    String resource = TEST_RESOURCE_1;
    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.COMPRESS );
    clearTempFile();

    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.COMPRESS );

    File resourceFile = getResourceCopyFile( resource );
    assertFalse( "file must not be written twice", resourceFile.exists() );
  }

  public void testNotCompressedInDevelopmentMode() throws Exception {
    System.setProperty( SystemProps.CLIENT_LIBRARY_VARIANT,
                        SystemProps.DEBUG_CLIENT_LIBRARY_VARIANT );
    IResourceManager manager = getResourceManager( );
    String resource = TEST_RESOURCE_1;

    manager.register( resource, HTTP.CHARSET_UTF_8, RegisterOptions.COMPRESS );

    File resourceFile = getResourceCopyFile( resource );
    byte[] origin = read( openStream( resource ) );
    byte[] copy = read( resourceFile );
    assertTrue( "Resource not registered", manager.isRegistered( resource ) );
    assertTrue( "Resource was not written to disk", resourceFile.exists() );
    assertEquals( "Resource was compressed in development mode", origin.length, copy.length );
  }

  public void testUnregisterNonExistingResource() {
    IResourceManager manager = getResourceManager( );

    boolean unregistered = manager.unregister( "foo" );

    assertFalse( unregistered );
  }

  public void testUnregisterWithIllegalArgument() {
    IResourceManager manager = getResourceManager( );
    try {
      manager.unregister( null );
      fail( "Unregister must not allow null-argument" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testUnregister() {
    IResourceManager manager = getResourceManager( );
    manager.register( TEST_RESOURCE_1_JAR );

    boolean unregistered = manager.unregister( TEST_RESOURCE_1_JAR );

    assertTrue( unregistered );
    assertFalse( getResourceCopyFile( TEST_RESOURCE_1_JAR ).exists() );
  }

  public void testUnregisterVersionedResource() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    ResourceManagerImpl manager = getResourceManager( );
    String testResource = TEST_RESOURCE_1_VERSIONED;
    Integer version = manager.findVersion( testResource );
    String versionedResourceName
      = ResourceManagerImpl.versionedResourceName( testResource, version );
    manager.register( TEST_RESOURCE_1, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );

    boolean unregistered = manager.unregister( TEST_RESOURCE_1 );

    File resourceFile = getResourceCopyFile( versionedResourceName );
    assertTrue( unregistered );
    assertFalse( resourceFile.exists() );
  }

  public void testLocationRetrieval() {
    IResourceManager manager = getResourceManager( );
    manager.register( TEST_RESOURCE_1 );
    String location = manager.getLocation( TEST_RESOURCE_1 );
    assertEquals( "Different locations", TEST_LOCATION_DISK, location );
  }

  public void testVersionedLocationRetrieval() {
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "true" );
    IResourceManager manager = getResourceManager( );
    manager = getResourceManager( );

    manager.register( TEST_RESOURCE_1, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );

    String loc = manager.getLocation( TEST_RESOURCE_1 );
    assertEquals( "Different locations", TEST_LOCATION_VERSIONED_DISK, loc );
  }

  public void testRegisterWithCharset() throws Exception {
    IResourceManager manager = getResourceManager( );
    String charset = "ISO-8859-1";

    manager.register( ISO_RESOURCE, charset );

    byte[] expected = read( openStream( UTF_8_RESOURCE ) );
    File copiedFile = getResourceCopyFile( ISO_RESOURCE );
    byte[] actual = read( copiedFile );
    assertEquals( charset, manager.getCharset( ISO_RESOURCE ) );
    assertEquals( expected.length, actual.length );
    assertTrue( Arrays.equals( actual, expected ) );
  }

  public void testVersionedResourceName() {
    String name;
    Integer version = new Integer( 1 );
    name = ResourceManagerImpl.versionedResourceName( "path/to/name.ext", version );
    assertEquals( "path/to/name-1.ext", name );
    name = ResourceManagerImpl.versionedResourceName( "name.ext", version );
    assertEquals( "name-1.ext", name );
    name = ResourceManagerImpl.versionedResourceName( ".ext", version );
    assertEquals( "-1.ext", name );
    name = ResourceManagerImpl.versionedResourceName( ".", version );
    assertEquals( "-1.", name );
    name = ResourceManagerImpl.versionedResourceName( "", version );
    assertEquals( "-1", name );
    name = ResourceManagerImpl.versionedResourceName( "name", version );
    assertEquals( "name-1", name );
    String resource = "path.width.dot/andnamew/osuffix";
    name = ResourceManagerImpl.versionedResourceName( resource, version );
    assertEquals( "path.width.dot/andnamew/osuffix-1", name );
  }

  public void testGetLocationWithWrongParams() {
    IResourceManager manager = getResourceManager( );
    try {
      manager.getLocation( "trallala" );
      fail( "should not accept a not existing key." );
    } catch( RuntimeException expected ) {
    }

    try {
      manager.getLocation( null );
      fail( "Expected NullPointerException" );
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetRegisteredContent() throws Exception {
    IResourceManager manager = getResourceManager( );
    InputStream inputStream = openStream( TEST_RESOURCE_2 );
    String resourcename = "myfile";
    manager.register( resourcename, inputStream );
    inputStream.close();

    InputStream content = manager.getRegisteredContent( resourcename );

    assertNotNull( content );
    content.close();
    assertNull( manager.getRegisteredContent( "not-there" ) );
  }

  /*
   * 280582: resource registration fails when using ImageDescriptor.createFromURL
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280582
   */
  public void testRegisterWithInvalidFilename() throws Exception {
    IResourceManager manager = getResourceManager();
    InputStream inputStream = openStream( TEST_RESOURCE_2 );
    String name = "http://host:port/path$1";
    manager.register( name, inputStream );
    inputStream.close();

    String location = manager.getLocation( name );

    assertEquals( "rwt-resources/http$1//host$1port/path$$1", location );
  }

  public void testRegisterWithInputStreamClosesStream() {
    IResourceManager manager = getResourceManager();
    CloseableInputStream inputStream = new CloseableInputStream();

    manager.register( "resource-name", inputStream );

    assertTrue( inputStream.isClosed() );
  }

  public void testRegisterWithAlreadyRegisteredInputStreamClosesStream() {
    IResourceManager manager = getResourceManager();
    manager.register( "resource-name", new CloseableInputStream() );

    CloseableInputStream inputStream = new CloseableInputStream();
    manager.register( "resource-name", inputStream );

    assertTrue( inputStream.isClosed() );
  }

  protected void setUp() throws Exception {
    clearTempFile();
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    clearTempFile();
    Fixture.tearDown();
  }

  ///////////////////
  // helping methods

  private void assertEquals( byte[] origin, byte[] copy ) {
    assertEquals( "Content sizes are different", origin.length, copy.length );
    for( int i = 0; i < copy.length; i++ ) {
      assertEquals( "Content is different", origin[ i ], copy[ i ] );
    }
  }

  private static byte[] read( File file ) throws IOException {
    return read( new FileInputStream( file ) );
  }

  private static byte[] read( InputStream input ) throws IOException {
    BufferedInputStream bis = new BufferedInputStream( input );
    byte[] result = null;
    try {
      result = new byte[ bis.available() ];
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = ( byte )bis.read();
      }
    } finally {
      bis.close();
    }
    return result;
  }

  private static void clearTempFile() {
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_1_JAR ) );
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_1 ) );
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_1_VERSIONED ) );
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_2 ) );
    Fixture.delete( getResourceCopyFile( TEST_RESOURCE_3 ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_1_JAR ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_1 ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_1_VERSIONED ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_2 ) );
    Fixture.delete( getResourceCopyInTempFile( TEST_RESOURCE_3 ) );
  }

  private static File getResourceCopyFile( String resourceName ) {
    String path =   getWebContextDirectory()
                  + File.separator
                  + ResourceManagerImpl.RESOURCES
                  + File.separator
                  + resourceName;
    return new File( path );
  }

  private static File getResourceCopyInTempFile( String resourceName ) {
    String tempDir = System.getProperty( "java.io.tmpdir" );
    String user = System.getProperty( "user.name" );
    String sep = File.separator;
    String path = tempDir + sep + user + sep + "w4toolkit" + sep + resourceName;
    return new File( path );
  }

  private static ResourceManagerImpl getResourceManager() {
    DefaultResourceManagerFactory factory = new DefaultResourceManagerFactory();
    factory.setConfiguration( RWTFactory.getConfiguration() );
    return ( ResourceManagerImpl )factory.create();
  }

  private static String getWebContextDirectory() {
    return Fixture.WEB_CONTEXT_DIR.getPath();
  }

  private static InputStream openStream( String name ) {
    ClassLoader loader = ResourceManagerImpl_Test.class.getClassLoader();
    InputStream result = loader.getResourceAsStream( name );
    if( result == null ) {
      String encodedName = name.replace( '\\', '/' );
      result = loader.getResourceAsStream( encodedName );
    }
    return result;
  }
}
