/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine.configurables;

import java.text.MessageFormat;

import javax.servlet.ServletContext;

import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.internal.lifecycle.LifeCycleAdapterFactory;
import org.eclipse.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;


public class AdapterManagerConfigurable implements Configurable {
  public static final String ADAPTER_FACTORIES_PARAM = "org.eclipse.rwt.adapterFactories";

  private final ServletContext servletContext;
  
  private static class Declaration {
    private final AdapterFactory factory;
    private final Class adaptable;

    private Declaration( AdapterFactory factory, Class adaptable ) {
      this.factory = factory;
      this.adaptable = adaptable;
    }

    AdapterFactory getFactory() {
      return factory;
    }

    Class getAdaptable() {
      return adaptable;
    }
  }

  public AdapterManagerConfigurable( ServletContext servletContext ) {
    this.servletContext = servletContext;
  }

  public void configure( ApplicationContext context ) {
    Declaration[] declarations = getDeclarations();
    for( int i = 0; i < declarations.length; i++ ) {
      registerDeclarations( context, declarations[ i ] );
    }
  }

  public void reset( ApplicationContext context ) {
    context.getAdapterManager().deregisterAdapters();
  }

  private void registerDeclarations( ApplicationContext context, Declaration declaration ) {
    AdapterManager adapterManager = context.getAdapterManager();
    adapterManager.registerAdapters( declaration.getAdaptable(), declaration.getFactory() );
  }

  private Declaration[] getDeclarations() {
    Declaration[] result = getDefaultDeclarations();
    if( hasConfiguredDeclarations() ) {
      result = parseDeclarations();
    }
    return result;
  }

  private Declaration[] parseDeclarations() {
    String[] factoryAdaptablePairs = parseFactoryAdaptablePairs();
    Declaration[] result = new Declaration[ factoryAdaptablePairs.length ];
    for( int i = 0; i < factoryAdaptablePairs.length; i++ ) {
      result[ i ] = parseFactoryAdaptablePair( factoryAdaptablePairs[ i ].trim() );
    }
    return result;
  }

  private static Declaration parseFactoryAdaptablePair( String factoryAdaptablePair ) {
    checkIsPair( factoryAdaptablePair ); 
    Class adaptableClass = loadAdaptableClass( factoryAdaptablePair );
    Class factoryClass = loadFactoryClass( factoryAdaptablePair );
    AdapterFactory factory = createFactory( factoryClass );
    return new Declaration( factory, adaptableClass );
  }

  private static AdapterFactory createFactory( Class factoryClass ) {
    AdapterFactory factory;
    try {
      factory = ( AdapterFactory )ClassUtil.newInstance( factoryClass );
    } catch( ClassInstantiationException cie ) {
      String text = "Could not create an instance of the adapter factory class ''{0}''.";
      Object[] param = new Object[] { factoryClass.getName() };
      String msg = MessageFormat.format( text, param );
      throw new IllegalArgumentException( msg );
    }
    return factory;
  }

  private static Class loadFactoryClass( String factoryAdaptablePair ) {
    Class result;
    try {
      result = Class.forName( getFactoryClassName( factoryAdaptablePair ) );
    } catch( ClassNotFoundException cnfe ) {
      String text = "Could not load factory class ''{0}''.";
      Object[] param = new Object[] { getFactoryClassName( factoryAdaptablePair ) };
      throw new IllegalArgumentException( MessageFormat.format( text, param ) );
    }
    return result;
  }

  private static Class loadAdaptableClass( String factoryAdaptablePair ) {
    Class result;
    try {
      result = Class.forName( getAdaptableClassName( factoryAdaptablePair ) );
    } catch( ClassNotFoundException cnfe ) {
      String text = "Could not load adaptable class ''{0}''.";
      Object[] param = new Object[] { getAdaptableClassName( factoryAdaptablePair ) };
      throw new IllegalArgumentException( MessageFormat.format( text, param ) );
    }
    return result;
  }

  private static String getFactoryClassName( String factoryAdaptablePair ) {
    return getClassNames( factoryAdaptablePair )[ 0 ];
  }

  private static String getAdaptableClassName( String factoryAdaptablePair ) {
    return getClassNames( factoryAdaptablePair )[ 1 ];
  }

  private static void checkIsPair( String factoryAdaptablePair ) {
    String[] classNames = getClassNames( factoryAdaptablePair );
    if( classNames.length != 2 ) {
      String text = "''{0}'' is not a valid factory-adaptable pair.";
      Object[] param = new Object[] { factoryAdaptablePair };
      throw new IllegalArgumentException( MessageFormat.format( text, param ) );
    }
  }
  
  private static String[] getClassNames( String factoryAdaptablePair ) {
    return factoryAdaptablePair.split( RWTServletContextListener.PARAMETER_SPLIT );
  }

  private String[] parseFactoryAdaptablePairs() {
    String initParam = getInitParameter();
    return initParam.split( RWTServletContextListener.PARAMETER_SEPARATOR );
  }

  private boolean hasConfiguredDeclarations() {
    return null != getInitParameter();
  }
  
  private String getInitParameter() {
    return servletContext.getInitParameter( AdapterManagerConfigurable.ADAPTER_FACTORIES_PARAM );
  }
  
  private Declaration[] getDefaultDeclarations() {
    LifeCycleAdapterFactory lifeCycleAdapterFactory = new LifeCycleAdapterFactory();
    return new Declaration[] {
      new Declaration( lifeCycleAdapterFactory, Widget.class ),
      new Declaration( lifeCycleAdapterFactory, Display.class )
    };
  }
}