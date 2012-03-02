/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import org.eclipse.swt.internal.widgets.IdGenerator;


public class ClientObjectAdapter implements IClientObjectAdapter {

  private final String id;

  public ClientObjectAdapter() {
    this( "o" );
  }

  public ClientObjectAdapter( String customPrefix ) {
    id = IdGenerator.getInstance().newId( customPrefix );
  }

  public String getId() {
    return id;
  }

}
