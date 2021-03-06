/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.widgets;


/**
 * Listener adaptor with empty implementations of all listener methods.
 * @author stefan.roeck
 * @deprecated Use FileDialog or FileUpload implementation instead. 
 */
public class UploadAdapter implements UploadListener {

  public void uploadFinished( final UploadEvent uploadEvent ) {
  }

  public void uploadInProgress( final UploadEvent uploadEvent ) {
  }

  public void uploadException( final UploadEvent uploadEvent ) {
  }
}
