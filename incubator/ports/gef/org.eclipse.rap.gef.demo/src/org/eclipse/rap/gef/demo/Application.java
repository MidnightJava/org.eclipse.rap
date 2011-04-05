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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.draw2d.examples.cg.ObstructionExample;
import org.eclipse.draw2d.examples.connections.ConnectionAndAnchorExample;
import org.eclipse.draw2d.examples.graph.CompoundGraphDemo;
import org.eclipse.draw2d.examples.graph.DirectedGraphDemo;
import org.eclipse.draw2d.examples.layouts.FlowLayoutExample;
import org.eclipse.draw2d.examples.layouts.GridLayoutExample;
import org.eclipse.draw2d.examples.layouts.ToolbarLayoutExample;
import org.eclipse.draw2d.examples.scrollpane.ScrollPaneExample;
import org.eclipse.draw2d.examples.tree.SimpleTreeExample;
import org.eclipse.draw2d.examples.tree.TreeExample;
import org.eclipse.draw2d.rap.swt.SWT;
import org.eclipse.gef.examples.flow.ui.FlowEditorView;
import org.eclipse.gef.examples.logicdesigner.LogicEditorView;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * This class controls all aspects of the application's execution
 * and is contributed through the plugin.xml.
 */
public class Application implements IEntryPoint 
{
 
  public int createUI() 
  {
    Display display = PlatformUI.createDisplay();
    PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
    return 0;
  }
  
  public static class Draw2DExampleView extends ViewPart 
  {
    private Map examples;
    private Map controls;
    private StackLayout layout;
    private Composite demoContent; 
    
    public void createPartControl (final Composite parent) 
    {
      UICallBack.activate(getSite().getId()+getViewSite().getSecondaryId());
      SashForm form = new SashForm(parent, SWT.HORIZONTAL);
      final ListViewer exampleList = new ListViewer(form, SWT.V_SCROLL | SWT.BORDER);
      exampleList.setContentProvider(new ArrayContentProvider());
      exampleList.setLabelProvider(new LabelProvider());
      examples = new TreeMap();
      controls = new HashMap();
      demoContent = new Composite(form, SWT.BORDER);
      layout = new StackLayout();
      demoContent.setLayout(layout);
      layout.topControl = new Composite(demoContent, SWT.NONE);
      demoContent.update();
      demoContent.layout(true);
      examples.put("Compound Graph", new Runnable() {public void run() {new CompoundGraphDemo().run(demoContent);}});
      examples.put("Directed Graph", new Runnable() {public void run() {new DirectedGraphDemo().run(demoContent);}});
      examples.put("Connection and Anchor", new Runnable() {public void run() {new ConnectionAndAnchorExample().run(demoContent);}});
      examples.put("Obstruction", new Runnable() {public void run() {new ObstructionExample().run(demoContent);}});
//      examples.put("Ellipse Hitting", new Runnable() {public void run() {new EllipseHitting().run(demoContent);}});
      examples.put("Flow Layout", new Runnable() {public void run() {new FlowLayoutExample().run(demoContent);}});
      examples.put("Grid Layout", new Runnable() {public void run() {new GridLayoutExample().run(demoContent);}});
      examples.put("Toolbar Layout", new Runnable() {public void run() {new ToolbarLayoutExample().run(demoContent);}});
//      examples.put("Paths", new Runnable() {public void run() {new PathExample().run(demoContent);}});
      examples.put("Scroll Pane", new Runnable() {public void run() {new ScrollPaneExample().run(demoContent);}});
//      examples.put("Shape Styles", new Runnable() {public void run() {new ShapeStylesExample().run(demoContent);}});
//      examples.put("Thumbnail", new Runnable() {public void run() {new ThumbnailExample().run(demoContent);}});
      examples.put("Tree", new Runnable() {public void run() {new TreeExample().run(demoContent);}});
      examples.put("Simple Tree", new Runnable() {public void run() {new SimpleTreeExample().run(demoContent);}});
//      examples.put("Zoom", new Runnable() {public void run() {new ZoomExample().run(demoContent);}});
      
      
      exampleList.setInput(examples.keySet());
      exampleList.addSelectionChangedListener(new ISelectionChangedListener() {
        public void selectionChanged(SelectionChangedEvent event) {
          IStructuredSelection selection = (IStructuredSelection)event.getSelection();
          Object selElem = selection.getFirstElement();
          Object ctrl = controls.get(selElem);
          if (ctrl == null) {
            Runnable runner = (Runnable)examples.get(selElem);
            runner.run();
            Control[] children = demoContent.getChildren();
            ctrl = children[children.length-1];
            controls.put(selElem, ctrl);
          }
          layout.topControl = (Control)ctrl;
          demoContent.update();
          demoContent.layout(true);
        }
      });
      form.setWeights(new int[] {1,3});
    }
    
    public void setFocus() 
    {
    }
    
    public void dispose() {
      UICallBack.deactivate(getSite().getId()+getViewSite().getSecondaryId());
      super.dispose();
    }
  }
  
  public static class FlowExampleView extends FlowEditorView 
  {
    //further implementation coming    
  }
  
  public static class LogicExampleView extends LogicEditorView 
  {
   //further implementation coming 
  }
  
}
