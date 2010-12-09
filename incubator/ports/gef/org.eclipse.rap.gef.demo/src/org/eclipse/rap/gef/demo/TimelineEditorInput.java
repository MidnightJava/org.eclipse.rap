/******************************************************************************
 * Copyright (c) 2009-2010 Texas Center for Applied Technology
 * Texas Engineering Experiment Station
 * The Texas A&M University System
 * All Rights Reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Austin Riddle (Texas Center for Applied Technology) - 
 *                   initial demo implementation
 *****************************************************************************/
package org.eclipse.rap.gef.demo;

import org.eclipse.gef.examples.flow.model.ActivityDiagram;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Austin.Riddle
 *
 */
public class TimelineEditorInput implements IEditorInput {

    ActivityDiagram model = null;
    String name = "Activity Diagram Model";
    
    public Object getAdapter(Class adapter) {
      if (adapter.equals(ActivityDiagram.class)) {
        if (model == null) {
          model = new ActivityDiagram();
        }
        return model;
      }
      return null;
    }
  
    public boolean exists() {
      return false;
    }
  
    /**
     * sets the name of this input used for display purposes.
     * @param name
     */
    public void setName (String name) {
      this.name = name;
    }
  
    public String getName() {
      return name;
    }
  
    public IPersistableElement getPersistable() {
      return null;
    }
  
    public String getToolTipText() {
      return "Activity Diagram";
    }

    public ImageDescriptor getImageDescriptor() {
      return null;
    }
    
}
