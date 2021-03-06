/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd;

import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;


public interface IDNDAdapter {
  
  void cancel();
  boolean isCanceled();
  
  void setDetailChanged( Control control, int detail );
  void cancelDetailChanged();
  boolean hasDetailChanged();
  int getDetailChangedValue();
  Control getDetailChangedControl();
  
  void setFeedbackChanged( Control control, int feedback );  
  void cancelFeedbackChanged();
  boolean hasFeedbackChanged();
  int getFeedbackChangedValue();
  Control getFeedbackChangedControl();

  void setDataTypeChanged( Control control, TransferData dataType );  
  void cancelDataTypeChanged();
  boolean hasDataTypeChanged();
  TransferData getDataTypeChangedValue();
  Control getDataTypeChangedControl();
}
