/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Rüdiger Herrmann - bug 335112
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 *                - fixed Bug 355723
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycleAdapterUtil;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.internal.resources.ResourceUtil;
import org.eclipse.rwt.internal.theme.ThemePropertyAdapterRegistry.ThemePropertyAdapter;
import org.eclipse.rwt.internal.theme.css.CssElementHolder;
import org.eclipse.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.rwt.resources.ResourceLoader;
import org.eclipse.swt.widgets.Widget;


/**
 * The ThemeManager maintains information about the themeable widgets and the
 * installed themes.
 */
public class ThemeManager {

  public static final String DEFAULT_THEME_ID = "org.eclipse.rap.rwt.theme.Default";
  private static final String DEFAULT_THEME_NAME = "RAP Default Theme";

  // TODO [ApplicationContext]: made field public to replace with a performance
  //      optimized solution for tests. Think about a less intrusive solution.
  public static ResourceLoader STANDARD_RESOURCE_LOADER = new ResourceLoader() {
    ClassLoader classLoader = getClass().getClassLoader();
    public InputStream getResourceAsStream( final String resourceName ) throws IOException {
      return classLoader.getResourceAsStream( resourceName );
    }
  };
  
  /** Expected character set of JS files. */
  private static final String CHARSET = "UTF-8";

  private static final String LOG_SYSTEM_PROPERTY
    = System.getProperty( ThemeManager.class.getName() + ".log" );
  private static final boolean DEBUG = "true".equals( LOG_SYSTEM_PROPERTY );

  private static final String CLIENT_LIBRARY_VARIANT = "org.eclipse.rwt.clientLibraryVariant";
  private static final String DEBUG_CLIENT_LIBRARY_VARIANT = "DEBUG";

  private static final String WIDGET_THEME_PATH = "resource/widget/rap";

  static final String IMAGE_DEST_PATH = "themes/images";
  private static final String CURSOR_DEST_PATH = "themes/cursors";

  private static final Class[] THEMEABLE_WIDGETS = new Class[]{
    org.eclipse.swt.widgets.Widget.class,
    org.eclipse.swt.widgets.Control.class,
    org.eclipse.swt.widgets.Composite.class,
    org.eclipse.swt.widgets.Button.class,
    org.eclipse.swt.widgets.Combo.class,
    org.eclipse.swt.widgets.CoolBar.class,
    org.eclipse.swt.custom.CTabFolder.class,
    org.eclipse.swt.widgets.Group.class,
    org.eclipse.swt.widgets.Label.class,
    org.eclipse.swt.widgets.Link.class,
    org.eclipse.swt.widgets.List.class,
    org.eclipse.swt.widgets.Menu.class,
    org.eclipse.swt.widgets.ProgressBar.class,
    org.eclipse.swt.widgets.Shell.class,
    org.eclipse.swt.widgets.Spinner.class,
    org.eclipse.swt.widgets.TabFolder.class,
    org.eclipse.swt.widgets.Table.class,
    org.eclipse.swt.widgets.Text.class,
    org.eclipse.swt.widgets.ToolBar.class,
    org.eclipse.swt.widgets.Tree.class,
    org.eclipse.swt.widgets.Scale.class,
    org.eclipse.swt.widgets.DateTime.class,
    org.eclipse.swt.widgets.ExpandBar.class,
    org.eclipse.swt.widgets.Sash.class,
    org.eclipse.swt.widgets.Slider.class,
    org.eclipse.swt.widgets.ToolTip.class,
    org.eclipse.swt.custom.CCombo.class,
    org.eclipse.swt.custom.CLabel.class,
    org.eclipse.swt.browser.Browser.class,
    org.eclipse.swt.widgets.ScrollBar.class
  };

  private final Set<String> customAppearances;
  private final Map<String, Theme> themes;
  private final Set<String> registeredThemeFiles;
  private final ThemeableWidgetHolder themeableWidgets;
  private final CssElementHolder registeredCssElements;
  private final ThemeAdapterManager themeAdapterManager;
  private final Map<String, String> resolvedPackageNames; // only for performance improvements
  private Theme defaultTheme;
  private boolean initialized;
  private boolean widgetsInitialized;

  public ThemeManager() {
    initialized = false;
    widgetsInitialized = false;
    themeableWidgets = new ThemeableWidgetHolder();
    customAppearances = new HashSet<String>();
    registeredThemeFiles = new HashSet<String>();
    registeredCssElements = new CssElementHolder();
    themeAdapterManager = new ThemeAdapterManager();
    themes = new HashMap<String, Theme>();
    resolvedPackageNames = new HashMap<String, String>();
    addDefaultTheme();
  }

  public void activate() {
    initialize();
    registerResources();
  }

  public void deactivate() {
    resolvedPackageNames.clear();
    themes.clear();
    themeAdapterManager.reset();
    registeredCssElements.clear();
    registeredThemeFiles.clear();
    customAppearances.clear();
    themeableWidgets.reset();
    widgetsInitialized = false;
    initialized = false;
    addDefaultTheme();
  }
  
  /**
   * Initializes the ThemeManager. Theming-relevant files are loaded for all
   * themeable widgets, resources are registered. If the ThemeManager has
   * already been initialized, no action is taken.
   */
  private void initialize() {
    if( !initialized ) {
      initializeThemeableWidgets();
      Collection allThemes = themes.values();
      Iterator iterator = allThemes.iterator();
      ThemeableWidget[] allThemeableWidgets = themeableWidgets.getAll();
      while( iterator.hasNext() ) {
        Theme theme = ( Theme )iterator.next();
        theme.initialize( allThemeableWidgets );
      }
      initialized = true;
    }
  }

  private void initializeThemeableWidgets() {
    StyleSheet defaultThemeContributionsBuffer = defaultTheme.getStyleSheet();
    doInitializeThemableWidgets();
    defaultTheme.addStyleSheet( defaultThemeContributionsBuffer );
  }

  private void doInitializeThemableWidgets() {
    if( !widgetsInitialized ) {
      addDefaultThemableWidgets();
      ThemeableWidget[] widgets = themeableWidgets.getAll();
      for( int i = 0; i < widgets.length; i++ ) {
        processThemeableWidget( widgets[ i ] );
      }
      widgetsInitialized = true;
    }
  }

  private void addDefaultTheme() {
    defaultTheme = new Theme( DEFAULT_THEME_ID, DEFAULT_THEME_NAME, null );
    themes.put( DEFAULT_THEME_ID, defaultTheme );
  }

  /**
   * Adds a custom widget to the list of themeable widgets. Note that this
   * method must be called before <code>initialize</code>.
   *
   * @param widget the themeable widget to add, must not be <code>null</code>
   * @param loader the resource loader used to load theme resources like theme
   *          definitions etc. The resources to load follow a naming convention
   *          and must be resolved by the class loader. This argument must not
   *          be <code>null</code>.
   * @throws IllegalStateException if the ThemeManager is already initialized
   * @throws NullPointerException if a parameter is null
   * @throws IllegalArgumentException if the given widget is not a subtype of
   *           {@link Widget}
   */
  public void addThemeableWidget( Class widget, ResourceLoader loader ) {
    checkNotInitialized();
    ParamCheck.notNull( widget, "widget" );
    ParamCheck.notNull( loader, "loader" );
    if( !Widget.class.isAssignableFrom( widget ) ) {
      String message = "Themeable widget is not a subtype of Widget: " + widget.getName();
      throw new IllegalArgumentException( message );
    }
    themeableWidgets.add( new ThemeableWidget( widget, loader ) );
  }
  

  public ThemeAdapterManager getThemeAdapterManager() {
    return themeAdapterManager;
  }

  /**
   * Registers a theme. Must be called before <code>initialize()</code>.
   *
   * @param theme the theme to register
   * @throws IllegalStateException if already initialized
   * @throws IllegalArgumentException if a theme with the same id is already
   *           registered
   */
  public void registerTheme( Theme theme ) {
    checkNotInitialized();
    String id = theme.getId();
    if( themes.containsKey( id ) ) {
      String pattern = "Theme with id ''{0}'' exists already";
      Object[] arguments = new Object[]{ id };
      String msg = MessageFormat.format( pattern, arguments );
      throw new IllegalArgumentException( msg );
    }
    themes.put( id, theme );
  }
  
  /**
   * Determines whether a theme with the specified id has been registered.
   *
   * @param themeId the id to check for
   * @return <code>true</code> if a theme has been registered with the given
   *         id
   */
  public boolean hasTheme( String themeId ) {
    return themes.containsKey( themeId );
  }

  /**
   * Returns the theme registered with the given id.
   *
   * @param themeId the id of the theme to retrieve
   * @return the theme registered with the given id or <code>null</code> if
   *         there is no theme registered with this id
   */
  public Theme getTheme( String themeId ) {
    Theme result = null;
    if( themes.containsKey( themeId ) ) {
      result = themes.get( themeId );
    }
    return result;
  }

  /**
   * Returns a list of all registered themes.
   *
   * @return an array that contains the ids of all registered themes, never
   *         <code>null</code>
   */
  public String[] getRegisteredThemeIds() {
    String[] result = new String[ themes.size() ];
    return themes.keySet().toArray( result );
  }

  /**
   * Generates and registers JavaScript code that installs the registered themes
   * on the client.
   *
   * @throws IllegalStateException if not initialized
   */
  public void registerResources() {
    checkInitialized();
    Iterator iterator = themes.keySet().iterator();
    // default theme must be rendered first
    registerThemeFiles( defaultTheme );
    while( iterator.hasNext() ) {
      String key = ( String )iterator.next();
      Theme theme = themes.get( key );
      if( theme != defaultTheme ) {
        registerThemeFiles( theme );
      }
    }
  }

  public ThemeableWidget getThemeableWidget( Class widget ) {
    return themeableWidgets.get( widget );
  }

  ThemeableWidget[] getAllThemeableWidget() {
    return themeableWidgets.getAll();
  }

  private void checkNotInitialized() {
    if( initialized ) {
      throw new IllegalStateException( "ThemeManager is already initialized" );
    }
  }

  private void checkInitialized() {
    if( !initialized ) {
      throw new IllegalStateException( "ThemeManager not initialized" );
    }
  }

  private void addDefaultThemableWidgets() {
    for( int i = 0; i < THEMEABLE_WIDGETS.length; i++ ) {
      addThemeableWidget( THEMEABLE_WIDGETS[ i ], STANDARD_RESOURCE_LOADER );
    }
  }

  /**
   * Loads and processes all theme-relevant resources for a given widget.
   */
  private void processThemeableWidget( final ThemeableWidget themeWidget ) {
    String className = LifeCycleAdapterUtil.getSimpleClassName( themeWidget.widget );
    String[] variants = LifeCycleAdapterUtil.getKitPackageVariants( themeWidget.widget );
    boolean found = false;
    try {
      for( int i = 0; i < variants.length && !found ; i++ ) {
        found |= loadThemeDef( themeWidget, variants[ i ], className );
        found |= loadAppearanceJs( themeWidget, variants[ i ], className );
        found |= loadDefaultCss( themeWidget, variants[ i ], className );
      }
      if( themeWidget.elements == null ) {
        log( "WARNING: No elements defined for themeable widget: " + themeWidget.widget.getName() );
      }
      if( themeWidget.defaultStyleSheet != null ) {
        defaultTheme.addStyleSheet( themeWidget.defaultStyleSheet );
      }
    } catch( IOException e ) {
      String msg = "Failed to initialize themeable widget: " + themeWidget.widget.getName();
      throw new ThemeManagerException( msg, e );
    }
  }

  private boolean loadThemeDef( ThemeableWidget themeWidget, String pkgName, String className )
    throws IOException
  {
    boolean result = false;
    String resPkgName = resolvePackageName( pkgName );
    String fileName = resPkgName + "/" + className + ".theme.xml";
    InputStream inStream = themeWidget.loader.getResourceAsStream( fileName );
    if( inStream != null ) {
      log( "Found theme definition file: " +  fileName );
      result = true;
      try {
        ThemeDefinitionReader reader = new ThemeDefinitionReader( inStream, fileName );
        reader.read();
        themeWidget.elements = reader.getThemeCssElements();
        for( int i = 0; i < themeWidget.elements.length; i++ ) {
          registeredCssElements.addElement( themeWidget.elements[ i ] );
        }
      } catch( final Exception e ) {
        String message = "Failed to parse theme definition file " + fileName;
        throw new ThemeManagerException( message, e );
      } finally {
        inStream.close();
      }
    }
    return result;
  }
  
  private String resolvePackageName( String packageName ) {
    String result = resolvedPackageNames.get( packageName );
    if( result == null ) {
      result =  packageName.replace( '.', '/' );
      resolvedPackageNames.put( packageName, result );
    }
    return result;
  }

  private boolean loadAppearanceJs( ThemeableWidget themeWidget, String pkgName, String className )
    throws IOException
  {
    boolean result = false;
    String resPkgName = resolvePackageName( pkgName );
    String fileName = resPkgName + "/" + className + ".appearances.js";
    InputStream inStream = themeWidget.loader.getResourceAsStream( fileName );
    if( inStream != null ) {
      log( "Found appearance js file: " +  fileName );
      try {
        String content = AppearancesUtil.readAppearanceFile( inStream );
        customAppearances.add( content );
        result = true;
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  private boolean loadDefaultCss( ThemeableWidget themeWidget, String pkgName, String className )
    throws IOException
  {
    boolean result = false;
    String resPkgName = resolvePackageName( pkgName );
    String fileName = resPkgName + "/" + className + ".default.css";
    ResourceLoader resLoader = themeWidget.loader;
    InputStream inStream = resLoader.getResourceAsStream( fileName );
    if( inStream != null ) {
      log( "Found default css file: " +  fileName );
      try {
        // TODO [rst] Check for illegal element names in selector list
        themeWidget.defaultStyleSheet
          = CssFileReader.readStyleSheet( inStream, fileName, resLoader );
        result = true;
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  /**
   * Creates and registers all JavaScript theme files and images for a given
   * theme.
   */
  private void registerThemeFiles( Theme theme ) {
    boolean compress = !isDebugVariant();
    synchronized( registeredThemeFiles ) {
      String themeId = theme.getId();
      if( !registeredThemeFiles.contains( themeId ) ) {
        String jsId = theme.getJsId();
        registerThemeableWidgetImages( theme );
        registerThemeableWidgetCursors( theme );
        StringBuffer sb = new StringBuffer();
        sb.append( createQxThemes( theme ) );
        // TODO [rst] Optimize: create only one ThemeStoreWriter for all themes
        IThemeCssElement[] elements = registeredCssElements.getAllElements();
        ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
        storeWriter.addTheme( theme, theme == defaultTheme );
        sb.append( storeWriter.createJs() );
        String themeCode = sb.toString();
        log( "-- REGISTERED THEME CODE FOR " + themeId + " ( " + themeCode.length() + " )--" );
        log( themeCode );
        log( "-- END REGISTERED THEME CODE --" );
        String name = resolvePackageName( jsId ) + ".js";
        registerJsLibrary( name, themeCode, compress );
        registeredThemeFiles.add( themeId );
      }
    }
  }

  private void registerThemeableWidgetImages( Theme theme ) {
    QxType[] values = theme.getValuesMap().getAllValues();
    for( int i = 0; i < values.length; i++ ) {
      QxType value = values[ i ];
      if( value instanceof QxImage ) {
        QxImage image = ( QxImage )value;
        if( !image.none ) {
          InputStream inputStream;
          try {
            inputStream = image.loader.getResourceAsStream( image.path );
          } catch( IOException e ) {
            String message = "Failed to load resource " + image.path;
            throw new ThemeManagerException( message, e );
          }
          if( inputStream == null ) {
            String pattern = "Resource ''{0}'' not found for theme ''{1}''";
            Object[] arguments = new Object[]{ image.path, theme.getName() };
            String mesg = MessageFormat.format( pattern, arguments  );
            throw new IllegalArgumentException( mesg );
          }
          try {
            ThemePropertyAdapterRegistry registry = ThemePropertyAdapterRegistry.getInstance();
            ThemePropertyAdapter adapter = registry.getPropertyAdapter( value.getClass() );
            String key = adapter.getKey( value );
            String registerPath = IMAGE_DEST_PATH + "/" + key;
            getResourceManager().register( registerPath, inputStream );
          } finally {
            try {
              inputStream.close();
            } catch( final IOException e ) {
              throw new RuntimeException( e );
            }
          }
        }
      }
    }
  }

  private void registerThemeableWidgetCursors( Theme theme ) {
    QxType[] values = theme.getValuesMap().getAllValues();
    for( int i = 0; i < values.length; i++ ) {
      QxType value = values[ i ];
      if( value instanceof QxCursor ) {
        QxCursor cursor = ( QxCursor )value;
        if( cursor.isCustomCursor() ) {
          ThemePropertyAdapterRegistry registry = ThemePropertyAdapterRegistry.getInstance();
          ThemePropertyAdapter adapter = registry.getPropertyAdapter( value.getClass() );
          String key = adapter.getKey( value );
          String path = cursor.value;
          log( " register theme cursor " + key + ", path=" + path );
          InputStream inputStream;
          try {
            inputStream = cursor.loader.getResourceAsStream( path );
          } catch( IOException e ) {
            String message = "Failed to load resource " + path;
            throw new ThemeManagerException( message, e );
          }
          if( inputStream == null ) {
            String pattern = "Resource ''{0}'' not found for theme ''{1}''";
            Object[] arguments = new Object[]{ path, theme.getName() };
            String mesg = MessageFormat.format( pattern, arguments  );
            throw new IllegalArgumentException( mesg );
          }
          try {
            String widgetDestPath = CURSOR_DEST_PATH;
            String registerPath = widgetDestPath + "/" + key;
            getResourceManager().register( registerPath, inputStream );
            String location = getResourceManager().getLocation( registerPath );
            log( " theme cursor registered @ " + location );
          } finally {
            try {
              inputStream.close();
            } catch( final IOException e ) {
              throw new RuntimeException( e );
            }
          }
        }
      }
    }
  }

  private static void registerJsLibrary( String name, String code, boolean compress ) {
    IResourceManager resourceManager = getResourceManager();
    RegisterOptions option =   compress
                             ? RegisterOptions.VERSION_AND_COMPRESS
                             : RegisterOptions.VERSION;
    if( code != null ) {
      byte[] buffer;
      try {
        buffer = code.getBytes( CHARSET );
      } catch( final UnsupportedEncodingException shouldNotHappen ) {
        throw new RuntimeException( shouldNotHappen );
      }
      ByteArrayInputStream inputStream = new ByteArrayInputStream( buffer );
      resourceManager.register( name, inputStream, CHARSET, option );
    } else {
      resourceManager.register( name, CHARSET, option );
    }
    ResourceUtil.useJsLibrary( name );
  }

  private static IResourceManager getResourceManager() {
    return RWTFactory.getResourceManager();
  }

  private String createQxThemes( Theme theme ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( createQxTheme( theme, QxTheme.ICON ) );
    buffer.append( createQxTheme( theme, QxTheme.WIDGET ) );
    buffer.append( createQxTheme( theme, QxTheme.APPEARANCE ) );
    buffer.append( createQxTheme( theme, QxTheme.META ) );
    return buffer.toString();
  }

  private String createQxTheme( Theme theme, int type ) {
    String jsId = theme.getJsId();
    String base = null;
    if( type == QxTheme.APPEARANCE ) {
      base = "org.eclipse.swt.theme.AppearancesBase";
    }
    QxTheme qxTheme = new QxTheme( jsId, theme.getName(), type, base );
    if( type == QxTheme.WIDGET || type == QxTheme.ICON ) {
      // TODO [rh] remove hard-coded resource-manager-path-prefix
      String uri = ResourceManagerImpl.RESOURCES + "/" + WIDGET_THEME_PATH;
      qxTheme.appendUri( uri );
    } else if( type == QxTheme.APPEARANCE ) {
      Iterator iterator = customAppearances.iterator();
      while( iterator.hasNext() ) {
        String appearance = ( String )iterator.next();
        qxTheme.appendValues( appearance );
      }
    } else if( type == QxTheme.META ) {
      qxTheme.appendTheme( "icon", jsId + "Icons" );
      qxTheme.appendTheme( "widget", jsId + "Widgets" );
      qxTheme.appendTheme( "appearance", jsId + "Appearances" );
    }
    return qxTheme.getJsCode();
  }

  private static boolean isDebugVariant() {
    String libraryVariant = System.getProperty( CLIENT_LIBRARY_VARIANT );
    return DEBUG_CLIENT_LIBRARY_VARIANT.equals( libraryVariant );
  }

  private static void log( final String mesg ) {
    if( DEBUG ) {
      System.out.println( mesg );
    }
  }
}
