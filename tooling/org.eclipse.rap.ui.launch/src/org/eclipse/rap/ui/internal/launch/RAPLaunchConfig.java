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

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.pde.launching.IPDELauncherConstants;


public final class RAPLaunchConfig {

  public static final class BrowserMode {

    public static final BrowserMode INTERNAL
      = new BrowserMode( "INTERNAL" ); //$NON-NLS-1$
    public static final BrowserMode EXTERNAL
      = new BrowserMode( "EXTERNAL" ); //$NON-NLS-1$

    public static BrowserMode[] values() {
      return new BrowserMode[] { INTERNAL, EXTERNAL };
    }

    public static BrowserMode parse( final String name ) {
      BrowserMode result = null;
      BrowserMode[] knownValues = values();
      for( int i = 0; result == null && i < knownValues.length; i++ ) {
        if( knownValues[ i ].getName().equalsIgnoreCase( name ) ) {
          result = knownValues[ i ];
        }
      }
      if( result == null ) {
        String text = "Unknown BrowserMode ''{0}''."; //$NON-NLS-1$
        String msg = MessageFormat.format( text, new Object[] { name } );
        throw new IllegalArgumentException( msg );
      }
      return result;
    }

    private final String name;

    private BrowserMode( final String name ) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public String toString() {
      return name;
    }
  }

  public static final class LibraryVariant {

    public static final LibraryVariant STANDARD
      = new LibraryVariant( "STANDARD" ); //$NON-NLS-1$
    public static final LibraryVariant DEBUG
      = new LibraryVariant( "DEBUG" ); //$NON-NLS-1$

    public static LibraryVariant[] values() {
      return new LibraryVariant[] { STANDARD, DEBUG };
    }

    public static LibraryVariant parse( final String name ) {
      LibraryVariant result = null;
      LibraryVariant[] knownValues = values();
      for( int i = 0; result == null && i < knownValues.length; i++ ) {
        if( knownValues[ i ].getName().equalsIgnoreCase( name ) ) {
          result = knownValues[ i ];
        }
      }
      if( result == null ) {
        String text = "Unknown LibraryVariant ''{0}''."; //$NON-NLS-1$
        String msg = MessageFormat.format( text, new Object[] { name } );
        throw new IllegalArgumentException( msg );
      }
      return result;
    }

    private final String name;

    private LibraryVariant( final String name ) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public String toString() {
      return name;
    }
  }

  public static final int MIN_PORT_NUMBER = 0;
  public static final int MAX_PORT_NUMBER = 65535;
  public static final int MIN_SESSION_TIMEOUT = 0;
  public static final int MAX_SESSION_TIMEOUT = Integer.MAX_VALUE;

  // Launch configuration attribute names
  private static final String PREFIX = "org.eclipse.rap.launch."; //$NON-NLS-1$
  public static final String SERVLET_NAME
    = PREFIX + "servletName"; //$NON-NLS-1$
  public static final String ENTRY_POINT
    = PREFIX + "entryPoint"; //$NON-NLS-1$
  public static final String TERMINATE_PREVIOUS
    = PREFIX + "terminatePrevious"; //$NON-NLS-1$
  public static final String OPEN_BROWSER
    = PREFIX + "openBrowser"; //$NON-NLS-1$
  public static final String BROWSER_MODE
    = PREFIX + "browserMode"; //$NON-NLS-1$
  public static final String PORT
    = PREFIX + "port"; //$NON-NLS-1$
  public static final String USE_MANUAL_PORT
    = PREFIX + "useManualPort"; //$NON-NLS-1$
  public static final String CONTEXTPATH
    = PREFIX + "contextpath"; //$NON-NLS-1$
  public static final String USE_MANUAL_CONTEXTPATH
    = PREFIX + "useManualContextPath"; //$NON-NLS-1$
  public static final String SESSION_TIMEOUT
    = PREFIX + "sessionTimeout"; //$NON-NLS-1$  
  public static final String USE_SESSION_TIMEOUT
    = PREFIX + "useSessionTimeout"; //$NON-NLS-1$
  public static final String LIBRARY_VARIANT
    = PREFIX + "libraryVariant"; //$NON-NLS-1$
  public static final String USE_DEFAULT_DATA_LOCATION 
    = PREFIX + "useDefaultDataLocation"; //$NON-NLS-1$
  public static final String DATA_LOCATION 
  = PREFIX + "dataLocation"; //$NON-NLS-1$

  // Default values for launch configuration attribute names
  private static final String DEFAULT_SERVLET_NAME = "rap"; //$NON-NLS-1$
  private static final String DEFAULT_ENTRY_POINT = ""; //$NON-NLS-1$
  private static final boolean DEFAULT_TERMINATE_PREVIOUS = true;
  private static final BrowserMode DEFAULT_BROWSER_MODE = BrowserMode.INTERNAL;
  private static final int DEFAULT_PORT = 10080;
  private static final boolean DEFAULT_USE_MANUAL_PORT = false;
  private static final String DEFAULT_CONTEXTPATH = "";
  private static final boolean DEFAULT_USE_MANUAL_CONTEXTPATH = false;
  private static final int DEFAULT_SESSION_TIMEOUT = MIN_SESSION_TIMEOUT;
  private static final boolean DEFAULT_USE_SESSION_TIMEOUT = false;
  private static final String DEFAULT_LIBRARY_VARIANT
    = LibraryVariant.STANDARD.getName();
  private static final String DEFAULT_DATA_LOCATION
    = "${workspace_loc}/.metadata/.plugins/"; //$NON-NLS-1$

  public static void setDefaults( ILaunchConfigurationWorkingCopy config ) {
    config.setAttribute( SERVLET_NAME, DEFAULT_SERVLET_NAME );
    config.setAttribute( ENTRY_POINT, DEFAULT_ENTRY_POINT );
    config.setAttribute( TERMINATE_PREVIOUS, DEFAULT_TERMINATE_PREVIOUS );
    config.setAttribute( BROWSER_MODE, DEFAULT_BROWSER_MODE.getName() );
    config.setAttribute( PORT, DEFAULT_PORT );
    config.setAttribute( USE_MANUAL_PORT, DEFAULT_USE_MANUAL_PORT );
    config.setAttribute( CONTEXTPATH, DEFAULT_CONTEXTPATH );
    config.setAttribute( USE_MANUAL_CONTEXTPATH, DEFAULT_USE_MANUAL_CONTEXTPATH );
    config.setAttribute( SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT );
    config.setAttribute( USE_SESSION_TIMEOUT, DEFAULT_USE_SESSION_TIMEOUT );
    config.setAttribute( LIBRARY_VARIANT, DEFAULT_LIBRARY_VARIANT );
    config.setAttribute( USE_DEFAULT_DATA_LOCATION, true );
    config.setAttribute( IPDELauncherConstants.DOCLEAR, false );
    config.setAttribute( IPDELauncherConstants.ASKCLEAR, false );
    String defaultDataLocation = getDefaultDataLocation( config.getName() );
    config.setAttribute( DATA_LOCATION, defaultDataLocation );
  }

  public static String getDefaultDataLocation( String name ) {
    return DEFAULT_DATA_LOCATION + Activator.getPluginId() + "/"+ name.replaceAll( "\\s", "" );
  }

  private final ILaunchConfiguration config;
  private final ILaunchConfigurationWorkingCopy workingCopy;

  public RAPLaunchConfig( final ILaunchConfiguration config ) {
    this.config = config;
    if( config instanceof ILaunchConfigurationWorkingCopy ) {
      workingCopy = ( ILaunchConfigurationWorkingCopy )config;
    } else {
      workingCopy = null;
    }
  }

  public String getName() {
    return config.getName();
  }
  
  public ILaunchConfiguration getUnderlyingLaunchConfig() {
    return config;
  }

  public RAPLaunchConfigValidator getValidator() {
    return new RAPLaunchConfigValidator( this );
  }

  //////////////////////////////////////////////////////////
  // Accessor and mutator methods for wrapped launch config

  public boolean getAskClearDataLocation() throws CoreException {
    return config.getAttribute( IPDELauncherConstants.ASKCLEAR, false );
  }

  public void setAskClearDataLocation( boolean askClear ) {
    checkWorkingCopy();
    workingCopy.setAttribute( IPDELauncherConstants.ASKCLEAR, askClear );
  }
  
  public boolean getDoClearDataLocation() throws CoreException {
    return config.getAttribute( IPDELauncherConstants.DOCLEAR, false );
  }

  public void setDoClearDataLocation( boolean doClear ) {
    checkWorkingCopy();
    workingCopy.setAttribute( IPDELauncherConstants.DOCLEAR, doClear );
  }

  public boolean getUseDefaultDatatLocation() throws CoreException {
    return config.getAttribute( USE_DEFAULT_DATA_LOCATION, true );
  }

  public void setUseDefaultDataLocation( boolean useDefaultDataLocation ) {
    checkWorkingCopy();
    workingCopy.setAttribute( USE_DEFAULT_DATA_LOCATION, useDefaultDataLocation );
  }

  public String getDataLocation() throws CoreException {
    return config.getAttribute( DATA_LOCATION, getDefaultDataLocation( getName() ) );
  }

  public void setDataLocation( String dataLocation ) {
    if( dataLocation == null ) {
      throw new NullPointerException( "dataLocation" ); //$NON-NLS-1$
    }
    checkWorkingCopy();
    workingCopy.setAttribute( DATA_LOCATION, dataLocation );
  }
  
  public String getServletName() throws CoreException {
    return config.getAttribute( SERVLET_NAME, DEFAULT_SERVLET_NAME );
  }

  public void setServletName( final String servletName ) {
    if( servletName == null ) {
      throw new NullPointerException( "servletName" ); //$NON-NLS-1$
    }
    checkWorkingCopy();
    workingCopy.setAttribute( SERVLET_NAME, servletName );
  }

  public String getEntryPoint() throws CoreException {
    return config.getAttribute( ENTRY_POINT, DEFAULT_ENTRY_POINT );
  }

  public void setEntryPoint( final String entryPoint ) {
    if( entryPoint == null ) {
      throw new NullPointerException( "entryPoint" ); //$NON-NLS-1$
    }
    checkWorkingCopy();
    workingCopy.setAttribute( ENTRY_POINT, entryPoint );
  }

  public boolean getTerminatePrevious() throws CoreException {
    return config.getAttribute( TERMINATE_PREVIOUS,
                                DEFAULT_TERMINATE_PREVIOUS );
  }

  public void setTerminatePrevious( final boolean terminatePrevious ) {
    checkWorkingCopy();
    workingCopy.setAttribute( TERMINATE_PREVIOUS, terminatePrevious );
  }

  public boolean getOpenBrowser() throws CoreException {
    return config.getAttribute( OPEN_BROWSER, true );
  }

  public void setOpenBrowser( final boolean openBrowser ) {
    checkWorkingCopy();
    workingCopy.setAttribute( OPEN_BROWSER, openBrowser );
  }

  public BrowserMode getBrowserMode() throws CoreException {
    String value
      = config.getAttribute( BROWSER_MODE, BrowserMode.INTERNAL.getName() );
    return BrowserMode.parse( value );
  }

  public void setBrowserMode( final BrowserMode browserMode ) {
    checkWorkingCopy();
    workingCopy.setAttribute( BROWSER_MODE, browserMode.getName() );
  }

  public boolean getUseManualPort() throws CoreException {
    // If not specified, return false instead of the default value (true) to
    // remain backwards compatible
    return config.getAttribute( USE_MANUAL_PORT, false );
  }

  public void setUseManualPort( final boolean useManualPort  ) {
    checkWorkingCopy();
    workingCopy.setAttribute( USE_MANUAL_PORT, useManualPort );
  }

  public int getPort() throws CoreException {
    return config.getAttribute( PORT, DEFAULT_PORT );
  }

  public void setPort( final int port ) {
    checkWorkingCopy();
    workingCopy.setAttribute( PORT, port );
  }
  
  public boolean getUseManualContextPath() throws CoreException {
    return config.getAttribute( USE_MANUAL_CONTEXTPATH, DEFAULT_USE_MANUAL_CONTEXTPATH );
  }

  public void setUseManualContextPath( boolean useManualContextPath ) {
    checkWorkingCopy();
    workingCopy.setAttribute( USE_MANUAL_CONTEXTPATH, useManualContextPath );
  }

  public String getContextPath() throws CoreException {
    return config.getAttribute( CONTEXTPATH, DEFAULT_CONTEXTPATH );
  }

  public void setContextPath( String contextPath ) {
    if( contextPath == null ) {
      throw new NullPointerException( "contextPath" ); //$NON-NLS-1$
    }
    checkWorkingCopy();
    workingCopy.setAttribute( CONTEXTPATH, contextPath );
  }
  
  public boolean getUseSessionTimeout() throws CoreException {
    return config.getAttribute( USE_SESSION_TIMEOUT, false );
  }

  public void setUseSessionTimeout( final boolean useSessionTimeout  ) {
    checkWorkingCopy();
    workingCopy.setAttribute( USE_SESSION_TIMEOUT, useSessionTimeout );
  }

  public int getSessionTimeout() throws CoreException {
    return config.getAttribute( SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT );
  }

  public void setSessionTimeout( final int timeout ) {
    checkWorkingCopy();
    workingCopy.setAttribute( SESSION_TIMEOUT, timeout );
  }

  public LibraryVariant getLibraryVariant() throws CoreException {
    String value
      = config.getAttribute( LIBRARY_VARIANT, DEFAULT_LIBRARY_VARIANT );
    return LibraryVariant.parse( value );
  }

  public void setLibraryVariant( final LibraryVariant libraryVariant ) {
    checkWorkingCopy();
    workingCopy.setAttribute( LIBRARY_VARIANT, libraryVariant.getName() );
  }
  
  /////////////////
  // Helping method

  private void checkWorkingCopy() {
    if( workingCopy == null ) {
      String msg
        = "Launch configuration cannot be modified, no working copy available"; //$NON-NLS-1$
      throw new IllegalStateException( msg );
    }
  }
}
