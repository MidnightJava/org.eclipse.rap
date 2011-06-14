/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rwt.widgets;


/**
 * Provides a configuration mechanism for the file upload. Note that this
 * configuration is shared for all upload widgets.
 * 
 * @author Stefan.Roeck
 * @deprecated Use FileDialog or FileUpload implementation instead.
 */
public interface IUploadConfiguration {

  /**
   *@see FileUploadBase#setFileSizeMax(long)
   */
  public void setFileMaxSize( long fileSizeMax );

  /**
   *@see FileUploadBase#getFileSizeMax()
   */
  public long getFileSizeMax();

}
