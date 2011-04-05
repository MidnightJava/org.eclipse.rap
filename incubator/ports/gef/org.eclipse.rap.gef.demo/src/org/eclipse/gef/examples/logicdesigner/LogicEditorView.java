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
package org.eclipse.gef.examples.logicdesigner;

import org.eclipse.draw2d.rap.swt.SWT;
import org.eclipse.rap.gef.demo.DirectEditorInput;
import org.eclipse.rap.gef.demo.EditorView;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class LogicEditorView extends EditorView {
  
  public LogicEditorView () {
    super();
    editor = new LogicEditor();
    init();
  }
  
  private void init () {
    try {
      IWorkbench workbench = PlatformUI.getWorkbench();
      DirectEditorInput input = new DirectEditorInput();
      input.setName("Logic Example");
      ((LogicEditor)editor).setInput(input);
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
