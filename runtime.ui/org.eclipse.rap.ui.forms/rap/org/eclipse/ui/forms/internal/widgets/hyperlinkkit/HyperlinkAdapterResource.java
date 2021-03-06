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

package org.eclipse.ui.forms.internal.widgets.hyperlinkkit;

import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;



public final class HyperlinkAdapterResource implements IResource {

  public String getCharset() {
    return "UTF-8"; //$NON-NLS-1$
  }

  public ClassLoader getLoader() {
    return HyperlinkAdapterResource.class.getClassLoader();
  }

  public String getLocation() {
    return "org/eclipse/ui/forms/widgets/HyperlinkAdapter.js"; //$NON-NLS-1$
  }

  public RegisterOptions getOptions() {
    return RegisterOptions.VERSION_AND_COMPRESS;
  }

  public boolean isExternal() {
    return false;
  }

  public boolean isJSLibrary() {
    return true;
  }
}
