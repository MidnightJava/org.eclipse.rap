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
package org.eclipse.gef.examples.flow.ui;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.draw2d.rap.swt.SWT;
import org.eclipse.gef.examples.flow.model.ActivityDiagram;
import org.eclipse.rap.gef.demo.EditorView;
import org.eclipse.rap.gef.demo.DirectEditorInput;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.ServiceRegistration;

public class FlowEditorView extends EditorView {
  
  public FlowEditorView () {
    super();
    editor = new FlowEditor();
    init();
  }
  
  private void init () {
    try {
      IWorkbench workbench = PlatformUI.getWorkbench();
      DirectEditorInput input = new DirectEditorInput();
      input.setName("Flow Example");
      ((FlowEditor)editor).setInput(input);
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void createPartControl(Composite parent) {
    Composite mainComposite = new Composite(parent, SWT.BORDER);
    mainComposite.setLayout(new FillLayout());
    super.createPartControl(mainComposite);
  }
  
  public void dispose() {
    editor.dispose();
    super.dispose();
  }
  
}
