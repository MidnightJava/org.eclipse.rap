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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationHistory;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.MultiPartInitException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Wraps an editor in a view
 */
public class EditorView extends ViewPart {

  protected EditorPart editor;
  
  /**
   * returns the editor wrapped by this view
   * @return
   */
  public EditorPart getEditor () {
    return editor;
  }
  
  public Object getAdapter(Class adapter) {
    Object o = null;
    if (editor != null) o = editor.getAdapter(adapter);
    if (o == null) return super.getAdapter(adapter);
    return o;
  }
  
  public void createPartControl(Composite parent) {
    UICallBack.activate(getSite().getId()+getViewSite().getSecondaryId());
    if (editor != null) 
      editor.createPartControl(parent); 
  }

  public void setFocus() {
    if (editor != null) { 
      editor.setFocus();
      activateOutlineHooks();
    }
  }
  
  public void dispose() {
    if (editor != null) deactivateOutlineHooks();
    UICallBack.deactivate(getSite().getId()+getViewSite().getSecondaryId());
    super.dispose();
  }
  
  protected void activateOutlineHooks () {
    IContentOutlinePage page = (IContentOutlinePage)editor.getAdapter(IContentOutlinePage.class);
    if (page != null) {
      IWorkbenchPage p = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      if (p != null) {
        IViewReference ref = p.findViewReference(IPageLayout.ID_OUTLINE);
        if (ref != null) {
          IViewPart vp = ref.getView(false);
          if (vp instanceof ContentOutline) {
            ((ContentOutline)vp).partActivated(editor);
          }
        }
      }
    }
  }
  protected void deactivateOutlineHooks () {
    IContentOutlinePage page = (IContentOutlinePage)editor.getAdapter(IContentOutlinePage.class);
    if (page != null) {
      IWorkbenchPage p = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      if (p != null) {
        IViewReference ref = p.findViewReference(IPageLayout.ID_OUTLINE);
        if (ref != null) {
          IViewPart vp = ref.getView(false);
          if (vp instanceof ContentOutline) {
            ((ContentOutline)vp).partClosed(editor);
          }
        }
      }
    }
  }

  public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
    super.setInitializationData(cfig, propertyName, data);
    if (editor != null)
      editor.setInitializationData(cfig, propertyName, data);
  }

  public void init(IViewSite site, IMemento memento) throws PartInitException {
    super.init(site, memento);
    if (editor != null && editor.getEditorInput() != null)
      editor.init(new EditorViewSite(site),editor.getEditorInput());
  }
  /**
   * 
   * Wraps a view site
   *
   */
  public class EditorViewSite implements IEditorSite {
    IViewSite vSite;
    IWorkbenchPage page;
    /**
     * wraps a view site
     * @param site
     */
    public EditorViewSite (IViewSite site) {
      vSite = site;
      page = new EditorViewSitePage();
    }
    
    public Object getService(Class api) {
      return vSite.getService(api);
    }
    public boolean hasService(Class api) {
      return vSite.hasService(api);
    }
    
    public IEditorActionBarContributor getActionBarContributor() {
      return null;
    }
    public IActionBars getActionBars() {
      return vSite.getActionBars();
    }
    public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
      registerContextMenu(menuManager,selectionProvider);
    }
    public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput) {
      registerContextMenu(menuId,menuManager,selectionProvider);
    }
    public IWorkbenchPage getPage() {
      return page;
    }
    public ISelectionProvider getSelectionProvider() {
      return vSite.getSelectionProvider();
    }
    public Shell getShell() {
      return vSite.getShell();
    }
    public IWorkbenchWindow getWorkbenchWindow() {
      return vSite.getWorkbenchWindow();
    }
    public void setSelectionProvider(ISelectionProvider provider) {
      vSite.setSelectionProvider(provider);
    }
    public Object getAdapter(Class adapter) {
      return vSite.getAdapter(adapter);
    }
    public String getId() {
      return vSite.getId();
    }
    public IWorkbenchPart getPart() {
      return vSite.getPart();
    }
    public String getPluginId() {
      return vSite.getPluginId();
    }
    public String getRegisteredName() {
      return vSite.getRegisteredName();
    }
    public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider) {
      vSite.registerContextMenu(menuManager,selectionProvider);      
    }
    public void registerContextMenu(String menuId, MenuManager menuManager, ISelectionProvider selectionProvider) {
      vSite.registerContextMenu(menuId,menuManager,selectionProvider);
    }
    
    class EditorViewSitePage implements IWorkbenchPage {
      public IEditorReference[] findEditors(IEditorInput input, String editorId, int matchFlags) {
        return vSite.getPage().findEditors(input, editorId, matchFlags);
      }
      public IWorkingSet getAggregateWorkingSet() {
        return getAggregateWorkingSet();
      }
      public int getPartState(IWorkbenchPartReference ref) {
        return vSite.getPage().getPartState(ref);
      }
      public IWorkbenchPartReference getReference(IWorkbenchPart part) {
        return vSite.getPage().getReference(part);
      }
      public IWorkingSet[] getWorkingSets() {
        return vSite.getPage().getWorkingSets();
      }
      public boolean isPageZoomed() {
        return vSite.getPage().isPageZoomed();
      }
      public IEditorPart openEditor(IEditorInput input, String editorId, boolean activate, int matchFlags) throws PartInitException {
        return vSite.getPage().openEditor(input, editorId,activate,matchFlags);
      }
      public void setPartState(IWorkbenchPartReference ref, int state) {
        vSite.getPage().setPartState(ref,state); 
      }
      public void setWorkingSets(IWorkingSet[] sets) {
        vSite.getPage().setWorkingSets(sets);
      }
      public void toggleZoom(IWorkbenchPartReference ref) {
        vSite.getPage().toggleZoom(ref);        
      }
      public void zoomOut() {
        vSite.getPage().zoomOut();
      }
      public void activate(IWorkbenchPart part) {
        vSite.getPage().activate(part);
      }
      public void addPropertyChangeListener(IPropertyChangeListener listener) {
        vSite.getPage().addPropertyChangeListener(listener);
      }
      public void bringToTop(IWorkbenchPart part) {
        vSite.getPage().bringToTop(part);
      }
      public boolean close() {
        return vSite.getPage().close();
      }
      public boolean closeAllEditors(boolean save) {
        return vSite.getPage().closeAllEditors(save);
      }
      public void closeAllPerspectives(boolean saveEditors, boolean closePage) {
        vSite.getPage().closeAllPerspectives(saveEditors,closePage);
      }
      public boolean closeEditor(IEditorPart ed, boolean save) {
        return vSite.getPage().closeEditor(ed,save);
      }
      public boolean closeEditors(IEditorReference[] editorRefs, boolean save) {
        return vSite.getPage().closeEditors(editorRefs,save);
      }
      public void closePerspective(IPerspectiveDescriptor desc, boolean saveEditors, boolean closePage) {
        vSite.getPage().closePerspective(desc,saveEditors,closePage);
      }
      public IEditorPart findEditor(IEditorInput input) {
        if (input.equals(editor.getEditorInput())) return editor;
        return vSite.getPage().findEditor(input);
      }
      public IViewPart findView(String viewId) {
        return vSite.getPage().findView(viewId);
      }
      public IViewReference findViewReference(String viewId, String secondaryId) {
        return vSite.getPage().findViewReference(viewId, secondaryId);
      }
      public IViewReference findViewReference(String viewId) {
        return vSite.getPage().findViewReference(viewId);
      }
      //THIS IS IMPORTANT
      public IEditorPart getActiveEditor() {
        if (EditorView.this.equals(vSite.getPage().getActivePart())) {
          return editor;
        }
        return vSite.getPage().getActiveEditor();
      }
      public IEditorPart[] getDirtyEditors() {
        return vSite.getPage().getDirtyEditors();
      }
      public IEditorReference[] getEditorReferences() {
        return vSite.getPage().getEditorReferences();
      }
      public int getEditorReuseThreshold() {
        return vSite.getPage().getEditorReuseThreshold();
      }
      public IEditorPart[] getEditors() {
        return vSite.getPage().getEditors();
      }
      public IExtensionTracker getExtensionTracker() {
        return vSite.getPage().getExtensionTracker();
      }
      public IAdaptable getInput() {
        return vSite.getPage().getInput();
      }
      public String getLabel() {
        return vSite.getPage().getLabel();
      }
      public INavigationHistory getNavigationHistory() {
        return vSite.getPage().getNavigationHistory();
      }
      public String[] getNewWizardShortcuts() {
        return vSite.getPage().getNewWizardShortcuts();
      }
      public IPerspectiveDescriptor[] getOpenPerspectives() {
        return vSite.getPage().getOpenPerspectives();
      }
      public IPerspectiveDescriptor getPerspective() {
        return vSite.getPage().getPerspective();
      }
      public String[] getPerspectiveShortcuts() {
        return vSite.getPage().getPerspectiveShortcuts();
      }
      public String[] getShowViewShortcuts() {
        return vSite.getPage().getShowViewShortcuts();
      }
      public IPerspectiveDescriptor[] getSortedPerspectives() {
        return vSite.getPage().getSortedPerspectives();
      }
      public IViewReference[] getViewReferences() {
        return vSite.getPage().getViewReferences();
      }
      public IViewPart[] getViews() {
        return vSite.getPage().getViews();
      }
      public IViewPart[] getViewStack(IViewPart part) {
        return vSite.getPage().getViewStack(part);
      }
      public IWorkbenchWindow getWorkbenchWindow() {
        return vSite.getPage().getWorkbenchWindow();
      }
      public IWorkingSet getWorkingSet() {
        return vSite.getPage().getWorkingSet();
      }
      public void hideActionSet(String actionSetID) {
        vSite.getPage().hideActionSet(actionSetID);
      }
      public void hideView(IViewPart view) {
        vSite.getPage().hideView(view);
      }
      public void hideView(IViewReference view) {
        vSite.getPage().hideView(view);
      }
      public boolean isEditorAreaVisible() {
        return vSite.getPage().isEditorAreaVisible();
      }
      public boolean isEditorPinned(IEditorPart ed) {
        return vSite.getPage().isEditorPinned(ed);
      }
      public boolean isPartVisible(IWorkbenchPart part) {
        return vSite.getPage().isPartVisible(part);
      }
      public IEditorPart openEditor(IEditorInput input, String editorId, boolean activate) throws PartInitException {
        return vSite.getPage().openEditor(input,editorId,activate);
      }
      public IEditorPart openEditor(IEditorInput input, String editorId) throws PartInitException {
        return vSite.getPage().openEditor(input,editorId);
      }
      public void removePropertyChangeListener(IPropertyChangeListener listener) {
        vSite.getPage().removePropertyChangeListener(listener);
      }
      public void resetPerspective() {
        vSite.getPage().resetPerspective();
      }
      public void reuseEditor(IReusableEditor ed, IEditorInput input) {
        vSite.getPage().reuseEditor(ed,input);
      }
      public boolean saveAllEditors(boolean confirm) {
        return vSite.getPage().saveAllEditors(confirm);
      }
      public boolean saveEditor(IEditorPart ed, boolean confirm) {
        return vSite.getPage().saveEditor(ed,confirm);
      }
      public void savePerspective() {
        vSite.getPage().savePerspective();
      }
      public void savePerspectiveAs(IPerspectiveDescriptor perspective) {
        vSite.getPage().savePerspectiveAs(perspective);
      }
      public void setEditorAreaVisible(boolean showEditorArea) {
        vSite.getPage().setEditorAreaVisible(showEditorArea);
      }
      public void setEditorReuseThreshold(int openEditors) {
        vSite.getPage().setEditorReuseThreshold(openEditors);
      }
      public void setPerspective(IPerspectiveDescriptor perspective) {
        vSite.getPage().setPerspective(perspective);
      }
      public void showActionSet(String actionSetID) {
        vSite.getPage().showActionSet(actionSetID);
      }
      public IViewPart showView(String viewId, String secondaryId, int mode) throws PartInitException {
        return vSite.getPage().showView(viewId,secondaryId,mode);
      }
      public IViewPart showView(String viewId) throws PartInitException {
        return vSite.getPage().showView(viewId);
      }
      public void addPartListener(IPartListener listener) {
        vSite.getPage().addPartListener(listener);
      }
      public void addPartListener(IPartListener2 listener) {
        vSite.getPage().addPartListener(listener);
      }
      ///THIS IS IMPORTANT
      public IWorkbenchPart getActivePart() {
        if (EditorView.this.equals(vSite.getPage().getActivePart())) {
          return editor;
        }
        return vSite.getPage().getActivePart();
      }
      public IWorkbenchPartReference getActivePartReference() {
        return vSite.getPage().getActivePartReference();
      }
      public void removePartListener(IPartListener listener) {
        vSite.getPage().removePartListener(listener);
      }
      public void removePartListener(IPartListener2 listener) {
        vSite.getPage().removePartListener(listener);
      }
      public void addPostSelectionListener(ISelectionListener listener) {
        vSite.getPage().addPostSelectionListener(listener);
      }
      public void addPostSelectionListener(String partId, ISelectionListener listener) {
        vSite.getPage().addPostSelectionListener(partId,listener);
      }
      public void addSelectionListener(ISelectionListener listener) {
        vSite.getPage().addSelectionListener(listener);
      }
      public void addSelectionListener(String partId, ISelectionListener listener) {
        vSite.getPage().addSelectionListener(partId,listener);
      }
      public ISelection getSelection() {
        return vSite.getPage().getSelection();
      }
      public ISelection getSelection(String partId) {
        return vSite.getPage().getSelection(partId);
      }
      public void removePostSelectionListener(ISelectionListener listener) {
        vSite.getPage().removePostSelectionListener(listener);
      }
      public void removePostSelectionListener(String partId, ISelectionListener listener) {
        vSite.getPage().removePostSelectionListener(partId,listener);
      }
      public void removeSelectionListener(ISelectionListener listener) {
        vSite.getPage().removeSelectionListener(listener);
      }
      public void removeSelectionListener(String partId, ISelectionListener listener) {
        vSite.getPage().removeSelectionListener(partId,listener);        
      }
      public void hideEditor(IEditorReference ref)
      {
         vSite.getPage().hideEditor(ref);
      }
      public IEditorReference[] openEditors(IEditorInput[] inputs, String[] editorIDs, int matchFlags) throws MultiPartInitException
      {
         return vSite.getPage().openEditors(inputs, editorIDs, matchFlags);
      }
      public void showEditor(IEditorReference ref)
      {
         vSite.getPage().showEditor(ref);
      }
      
    }
  }
}
