/*******************************************************************************
 * Copyright (c) 2007, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.launch;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.rap.ui.internal.launch.RAPLaunchConfig.LibraryVariant;
import org.eclipse.rap.ui.tests.Fixture;


public class RAPLaunchConfig_Test extends TestCase {
  
  private ILaunchConfigurationWorkingCopy config;
  private RAPLaunchConfig rapConfig;
  
  protected void setUp() throws Exception {
    config = Fixture.createRAPLaunchConfig();
    rapConfig = new RAPLaunchConfig( config );
  }
  
  protected void tearDown() throws Exception {
    config.delete();
  }
  
  public void testInitialValues() throws CoreException {
    assertFalse( rapConfig.getUseManualPort() );
    assertTrue( rapConfig.getTerminatePrevious() );
    assertFalse( "".equals( rapConfig.getServletName() ) );
    assertTrue( "".equals( rapConfig.getEntryPoint() ) );
    assertEquals( LibraryVariant.STANDARD, rapConfig.getLibraryVariant() );
    assertEquals( 0, rapConfig.getSessionTimeout() );
    assertFalse( rapConfig.getUseSessionTimeout() );
    assertFalse( rapConfig.getUseManualContextPath() );
    assertTrue( "".equals( rapConfig.getContextPath() ) );
  }
  
  public void testEntryPoint() throws CoreException {
    rapConfig.setEntryPoint( "ep-param" );
    assertEquals( "ep-param", rapConfig.getEntryPoint() );
    try {
      rapConfig.setEntryPoint( null );
      fail( "Must not allow to set entry point to null" );
    } catch( NullPointerException e ) {
      // expected
      assertEquals( "ep-param", rapConfig.getEntryPoint() );
    }
  }

  public void testServletName() throws CoreException {
    rapConfig.setServletName( "xyz" );
    assertEquals( "xyz", rapConfig.getServletName() );
    try {
      rapConfig.setServletName( null );
      fail( "Must not allow to set servlet name to null" );
    } catch( NullPointerException e ) {
      // expected
      assertEquals( "xyz", rapConfig.getServletName() );
    }
  }
  
  public void testSetDataLocation() throws CoreException {
    rapConfig.setDataLocation( "xyz" );
    assertEquals( "xyz", rapConfig.getDataLocation() );
  }
  
  public void testSetDataLocationThrowsExceptionWithNullValue() throws CoreException {
    rapConfig.setDataLocation( "xyz" );
    try {
      rapConfig.setDataLocation( null );
      fail( "Must not allow to set data location to null" );
    } catch( NullPointerException e ) {
      // expected
      assertEquals( "xyz", rapConfig.getDataLocation() );
    }
  }
  
  public void testSetContextPathValideValue() throws CoreException {
    rapConfig.setContextPath( "/xyz" );
    assertEquals( "/xyz", rapConfig.getContextPath() );
  }

  public void testSetContextPathInvalidValue() {
    try {
      rapConfig.setContextPath( null );
      fail( "Must not allow to set context path to null" );
    } catch( NullPointerException e ) {
      // expected
    }
  }
  
  public void testUseDefaultDataLocation() throws CoreException {
    rapConfig.setUseDefaultDataLocation( false );
    assertFalse( rapConfig.getUseDefaultDatatLocation() );
    rapConfig.setUseDefaultDataLocation( true );
    assertTrue( rapConfig.getUseDefaultDatatLocation() );
  }
  
  public void testDoClear() throws CoreException {
    rapConfig.setDoClearDataLocation( false );
    assertFalse( rapConfig.getDoClearDataLocation() );
    rapConfig.setDoClearDataLocation( true );
    assertTrue( rapConfig.getDoClearDataLocation() );
  }

}