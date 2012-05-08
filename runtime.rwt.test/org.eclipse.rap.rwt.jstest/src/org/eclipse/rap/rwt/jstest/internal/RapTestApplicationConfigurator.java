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
package org.eclipse.rap.rwt.jstest.internal;

import org.eclipse.rwt.application.Application;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.IEntryPointFactory;
import org.eclipse.rwt.service.IServiceHandler;


public class RapTestApplicationConfigurator implements ApplicationConfiguration {

  public void configure( Application application ) {
    IServiceHandler serviceHandler = new ClientResourcesServiceHandler();
    application.addServiceHandler( ClientResourcesServiceHandler.ID, serviceHandler );
    IEntryPointFactory factory = new IEntryPointFactory() {
      public IEntryPoint create() {
        return null;
      }
    };
    application.addEntryPoint( "/test", factory, null );
  }

}
