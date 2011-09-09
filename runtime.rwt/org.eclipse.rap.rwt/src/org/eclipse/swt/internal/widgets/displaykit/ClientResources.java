/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Rüdiger Herrmann - bug 335112
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.io.InputStream;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.resources.ResourceUtil;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.resources.*;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;


final class ClientResources {

  private static final String CLIENT_LIBRARY_VARIANT = "org.eclipse.rwt.clientLibraryVariant";
  private static final String DEBUG_CLIENT_LIBRARY_VARIANT = "DEBUG";

  private static final String CLIENT_JS = "client.js";

  private static final String[] JAVASCRIPT_FILES = new String[] {
    "debug-settings.js",
    "qx/core/Bootstrap.js",
    "qx/lang/Core.js",
    "qx/core/Setting.js",
    "qx/lang/Array.js",
    "qx/core/Variant.js",
    "org/eclipse/rwt/Client.js",
    "qx/lang/Object.js",
    "qx/Class.js",
    "qx/Mixin.js",
    "qx/core/MUserData.js",
    "qx/core/LegacyProperty.js",
    "qx/core/Property.js",
    "qx/lang/String.js",
    "qx/core/Object.js",
    "qx/lang/Function.js",
    "qx/bom/Viewport.js",
    "qx/Theme.js",
    "qx/core/Target.js",
    "qx/event/type/Event.js",
    "qx/event/type/DataEvent.js",
    "qx/event/type/ChangeEvent.js",
    "qx/client/Timer.js",
    "qx/html/String.js",
    "qx/dom/String.js",
    "qx/html/Entity.js",
    "qx/html/EventRegistration.js",
    "qx/core/Init.js",
    "qx/util/manager/MConnectedObject.js",
    "org/eclipse/rwt/HtmlUtil.js",
    "org/eclipse/rwt/protocol/AdapterRegistry.js",
    "org/eclipse/rwt/protocol/ObjectManager.js",
    "org/eclipse/rwt/protocol/AdapterUtil.js",
    "org/eclipse/rwt/protocol/EncodingUtil.js",
    "org/eclipse/rwt/Display.js",
    "org/eclipse/rwt/DisplayAdapter.js",
    "qx/ui/core/Widget.js",
    "org/eclipse/rwt/WidgetRenderAdapter.js",
    "qx/html/Dimension.js",
    "qx/html/Style.js",
    "qx/html/Scroll.js",
    "qx/html/StyleSheet.js",
    "qx/ui/core/Parent.js",
    "qx/event/type/FocusEvent.js",
    "org/eclipse/rwt/EventHandler.js",
    "qx/dom/Node.js",
    "org/eclipse/rwt/EventHandlerUtil.js",
    "qx/event/type/DomEvent.js",
    "qx/event/type/KeyEvent.js",
    "qx/event/type/MouseEvent.js",
    "qx/util/manager/Object.js",
    "qx/ui/embed/IframeManager.js",
    "qx/ui/layout/CanvasLayout.js",
    "qx/ui/layout/impl/LayoutImpl.js",
    "qx/lang/Number.js",
    "qx/ui/layout/impl/CanvasLayoutImpl.js",
    "qx/ui/core/ClientDocument.js",
    "qx/ui/basic/Terminator.js",
    "qx/ui/core/ClientDocumentBlocker.js",
    "qx/theme/manager/Appearance.js",
    "qx/theme/manager/Meta.js",
    "qx/util/manager/Value.js",
    "qx/theme/manager/Color.js",
    "qx/util/ColorUtil.js",
    "org/eclipse/rwt/Border.js",
    "qx/theme/manager/Font.js",
    "qx/ui/core/Font.js",
    "qx/theme/manager/Icon.js",
    "qx/io/Alias.js",
    "qx/theme/manager/Widget.js",
    "qx/event/handler/FocusHandler.js",
    "qx/bom/element/Location.js",
    "qx/bom/element/Style.js",
    "qx/bom/element/BoxSizing.js",
    "qx/bom/Document.js",
    "qx/bom/element/Overflow.js",
    "qx/io/image/Manager.js",
    "qx/html/Offset.js",
    "qx/html/ScrollIntoView.js",
    "qx/ui/layout/BoxLayout.js",
    "qx/ui/layout/impl/VerticalBoxLayoutImpl.js",
    "qx/util/Validation.js",
    "qx/ui/layout/impl/HorizontalBoxLayoutImpl.js",
    "qx/ui/basic/Atom.js",
    "org/eclipse/swt/widgets/LabelAdapter.js",
    "qx/ui/basic/Label.js",
    "qx/ui/basic/Image.js",
    "qx/io/image/PreloaderManager.js",
    "qx/io/image/Preloader.js",
    "qx/ui/form/ListItem.js",
    "qx/constant/Layout.js",
    "qx/constant/Style.js",
    "qx/io/remote/AbstractRemoteTransport.js",
    "qx/ui/layout/HorizontalBoxLayout.js",
    "qx/ui/form/Spinner.js",
    "qx/ui/form/TextField.js",
    "qx/ui/layout/VerticalBoxLayout.js",
    "qx/ui/form/Button.js",
    "qx/util/range/Range.js",
    "qx/ui/pageview/AbstractPageView.js",
    "qx/ui/pageview/tabview/TabView.js",
    "qx/ui/pageview/AbstractBar.js",
    "qx/ui/selection/RadioManager.js",
    "qx/ui/pageview/tabview/Bar.js",
    "qx/ui/pageview/AbstractPane.js",
    "qx/ui/pageview/tabview/Pane.js",
    "qx/ui/popup/Popup.js",
    "qx/ui/popup/PopupManager.js",
    "qx/ui/selection/SelectionManager.js",
    "qx/ui/selection/Selection.js",
    "org/eclipse/swt/widgets/AbstractSlider.js",
    "org/eclipse/rwt/widgets/ScrollBar.js",
    "qx/application/Gui.js",
    "qx/io/image/PreloaderSystem.js",
    "qx/io/remote/RequestQueue.js",
    "qx/io/remote/Exchange.js",
    "qx/io/remote/Response.js",
    "qx/util/Mime.js",
    "qx/io/remote/XmlHttpTransport.js",
    "qx/net/HttpRequest.js",
    "qx/html/Iframe.js",
    "qx/net/Http.js",
    "qx/io/remote/Request.js",
    "qx/ui/popup/PopupAtom.js",
    "qx/ui/popup/ToolTip.js",
    "qx/ui/popup/ToolTipManager.js",
    "qx/html/Window.js",
    "qx/client/History.js",
    "qx/event/handler/DragAndDropHandler.js",
    "qx/event/type/DragEvent.js",
    "qx/ui/embed/HtmlEmbed.js",
    "qx/ui/embed/Iframe.js",
    "qx/ui/pageview/AbstractButton.js",
    "qx/ui/groupbox/GroupBox.js",
    "qx/ui/resizer/MResizable.js",
    "qx/ui/resizer/ResizablePopup.js",
    "qx/ui/window/Window.js",
    "qx/ui/basic/HorizontalSpacer.js",
    "qx/ui/window/Manager.js",
    "qx/ui/menu/Separator.js",
    "qx/ui/pageview/AbstractPage.js",
    "qx/ui/pageview/tabview/Page.js",
    "qx/ui/pageview/tabview/Button.js",
    "org/eclipse/rwt/ErrorHandler.js",
    "org/eclipse/swt/LabelUtil.js",
    "org/eclipse/rwt/widgets/TreeRowContainer.js",
    "org/eclipse/rwt/TreeRowContainerWrapper.js",
    "org/eclipse/rwt/TreeUtil.js",
    "org/eclipse/rwt/widgets/TreeRow.js",
    "org/eclipse/swt/Application.js",
    "org/eclipse/rwt/AsyncKeyEventUtil.js",
    "org/eclipse/swt/Request.js",
    "org/eclipse/rwt/widgets/Menu.js",
    "org/eclipse/swt/EventUtil.js",
    "org/eclipse/rwt/FadeAnimationMixin.js",
    "org/eclipse/rwt/AnimationRenderer.js",
    "org/eclipse/rwt/Animation.js",
    "org/eclipse/rwt/widgets/WidgetToolTip.js",
    "org/eclipse/rwt/SyncKeyEventUtil.js",
    "org/eclipse/rwt/GraphicsMixin.js",
    "org/eclipse/rwt/GraphicsUtil.js",
    "org/eclipse/rwt/VML.js",
    "org/eclipse/rwt/SVG.js",
    "org/eclipse/swt/WidgetUtil.js",
    "org/eclipse/swt/theme/ThemeStore.js",
    "org/eclipse/rwt/widgets/MultiCellWidget.js",
    "org/eclipse/rwt/DomEventPatch.js",
    "org/eclipse/rwt/MenuManager.js",
    "org/eclipse/rwt/widgets/MenuItem.js",
    "org/eclipse/rwt/RadioButtonUtil.js",
    "org/eclipse/rwt/widgets/MenuBar.js",
    "org/eclipse/rwt/DNDSupport.js",
    "org/eclipse/swt/theme/ThemeValues.js",
    "org/eclipse/rwt/widgets/Tree.js",
    "org/eclipse/rwt/widgets/TreeItem.js",
    "org/eclipse/rwt/TreeDNDFeedback.js",
    "org/eclipse/swt/widgets/TableCellToolTip.js",
    "org/eclipse/rwt/widgets/TableHeader.js",
    "org/eclipse/swt/widgets/TableColumn.js",
    "org/eclipse/swt/browser/Browser.js",
    "org/eclipse/rwt/widgets/ExternalBrowser.js",
    "org/eclipse/swt/FontSizeCalculation.js",
    "org/eclipse/rwt/widgets/BasicButton.js",
    "org/eclipse/rwt/widgets/ToolItem.js",
    "org/eclipse/swt/widgets/Group.js",
    "org/eclipse/swt/widgets/Shell.js",
    "org/eclipse/swt/widgets/ShellAdapter.js",
    "org/eclipse/swt/widgets/ProgressBar.js",
    "org/eclipse/swt/widgets/Link.js",
    "org/eclipse/swt/widgets/Scrollable.js",
    "org/eclipse/swt/custom/ScrolledComposite.js",
    "org/eclipse/rwt/widgets/ToolBar.js",
    "org/eclipse/swt/TextUtil.js",
    "org/eclipse/swt/widgets/Scale.js",
    "org/eclipse/rwt/widgets/ToolSeparator.js",
    "org/eclipse/swt/theme/BorderDefinitions.js",
    "org/eclipse/rwt/widgets/BasicList.js",
    "org/eclipse/swt/widgets/Combo.js",
    "org/eclipse/swt/widgets/ComboAdapter.js",
    "org/eclipse/rwt/FocusIndicator.js",
    "org/eclipse/swt/CLabelUtil.js",
    "org/eclipse/swt/graphics/GC.js",
    "org/eclipse/rwt/VMLCanvas.js",
    "org/eclipse/swt/widgets/CompositeAdapter.js",
    "org/eclipse/swt/widgets/Composite.js",
    "org/eclipse/swt/widgets/Sash.js",
    "org/eclipse/swt/widgets/Canvas.js",
    "org/eclipse/swt/widgets/List.js",
    "org/eclipse/swt/TabUtil.js",
    "org/eclipse/swt/widgets/DateTimeCalendar.js",
    "org/eclipse/swt/widgets/Calendar.js",
    "org/eclipse/swt/widgets/CoolItem.js",
    "org/eclipse/rwt/widgets/Button.js",
    "org/eclipse/rwt/widgets/ButtonAdapter.js",
    "org/eclipse/rwt/widgets/FileUpload.js",
    "org/eclipse/swt/widgets/DateTimeTime.js",
    "org/eclipse/swt/widgets/Slider.js",
    "org/eclipse/swt/widgets/Spinner.js",
    "org/eclipse/swt/widgets/SpinnerAdapter.js",
    "org/eclipse/swt/widgets/DateTimeDate.js",
    "org/eclipse/swt/custom/CTabItem.js",
    "org/eclipse/swt/custom/CTabFolder.js",
    "org/eclipse/swt/widgets/ExpandItem.js",
    "org/eclipse/swt/widgets/ExpandBar.js",
    "org/eclipse/rwt/widgets/Text.js",
    "org/eclipse/rwt/widgets/TextAdapter.js",
    "org/eclipse/rwt/KeyEventUtil.js",
    "org/eclipse/swt/widgets/Separator.js",
    "org/eclipse/swt/widgets/SeparatorAdapter.js",
    "org/eclipse/swt/theme/AppearancesBase.js",
    "org/eclipse/rwt/widgets/ControlDecorator.js",
    "org/eclipse/rwt/MobileWebkitSupport.js",
    "org/eclipse/swt/widgets/ToolTip.js",
    "org/eclipse/swt/WidgetManager.js",
    "org/eclipse/rwt/protocol/Processor.js",
  };

  private static final String[] WIDGET_IMAGES = new String[] {
    "resource/static/image/blank.gif",
    "resource/static/image/dotted_white.gif",
    "resource/widget/rap/ctabfolder/maximize.gif",
    "resource/widget/rap/ctabfolder/minimize.gif",
    "resource/widget/rap/ctabfolder/restore.gif",
    "resource/widget/rap/ctabfolder/close.gif",
    "resource/widget/rap/ctabfolder/close_hover.gif",
    "resource/widget/rap/ctabfolder/ctabfolder-dropdown.png",
    "resource/widget/rap/cursors/alias.gif",
    "resource/widget/rap/cursors/copy.gif",
    "resource/widget/rap/cursors/move.gif",
    "resource/widget/rap/cursors/nodrop.gif",
    "resource/widget/rap/cursors/up_arrow.cur",
    "resource/widget/rap/scale/h_line.gif",
    "resource/widget/rap/scale/v_line.gif",
    "resource/widget/rap/scale/h_thumb.gif",
    "resource/widget/rap/scale/v_thumb.gif",
    "resource/widget/rap/scale/h_marker_big.gif",
    "resource/widget/rap/scale/v_marker_big.gif",
    "resource/widget/rap/scale/h_marker_small.gif",
    "resource/widget/rap/scale/v_marker_small.gif",
  };

  private final IResourceManager resourceManager;

  public ClientResources( IResourceManager resourceManager ) {
    this.resourceManager = resourceManager;
  }

  public void registerResources() {
    ClassLoader bufferedLoader = resourceManager.getContextLoader();
    try {
      resourceManager.setContextLoader( getClass().getClassLoader() );
      // TODO [rst] Needed by client.js - can we get rid of it?
      resourceManager.register( "resource/static/html/blank.html", HTTP.CHARSET_UTF_8 );
      registerJavascriptFiles();
      registerWidgetImages();
      registerContributions();
    } finally {
      resourceManager.setContextLoader( bufferedLoader );
    }
  }

  private void registerJavascriptFiles() {
    if( isDebug() ) {
      for( int i = 0; i < JAVASCRIPT_FILES.length; i++ ) {
        registerJavascriptFile( JAVASCRIPT_FILES[ i ] );
      }
    } else {
      registerJavascriptFile( CLIENT_JS );
    }
  }

  private void registerWidgetImages() {
    for( int i = 0; i < WIDGET_IMAGES.length; i++ ) {
      String resourcePath = WIDGET_IMAGES[ i ];
      InputStream inputStream = openResourceStream( resourcePath );
      resourceManager.register( resourcePath, inputStream );
    }
  }

  private void registerContributions() {
    IResource[] resources = RWTFactory.getResourceRegistry().get();
    for( int i = 0; i < resources.length; i++ ) {
      if( !resources[ i ].isExternal() ) {
        resourceManager.setContextLoader( resources[ i ].getLoader() );
        String charset = resources[ i ].getCharset();
        RegisterOptions options = resources[ i ].getOptions();
        String location = resources[ i ].getLocation();
        if( charset == null && options == null ) {
          resourceManager.register( location );
        } else if( options == null ) {
          resourceManager.register( location, charset );
        } else {
          resourceManager.register( location, charset, options );
        }
        if( resources[ i ].isJSLibrary() ) {
          ResourceUtil.useJsLibrary( location );
        }
      }
    }
  }

  private void registerJavascriptFile( String libraryName ) {
    InputStream inputStream = openResourceStream( libraryName );
    resourceManager.register( libraryName, inputStream, HTTP.CHARSET_UTF_8, RegisterOptions.VERSION );
    ResourceUtil.useJsLibrary( libraryName );
  }

  private InputStream openResourceStream( String name ) {
    InputStream result = getClass().getClassLoader().getResourceAsStream( name );
    if( result == null ) {
      String mesg = "Resource not found: " + name;
      throw new IllegalArgumentException( mesg );
    }
    return result;
  }

  private static boolean isDebug() {
    String libraryVariant = System.getProperty( CLIENT_LIBRARY_VARIANT );
    return DEBUG_CLIENT_LIBRARY_VARIANT.equals( libraryVariant );
  }
}
