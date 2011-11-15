/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.*;
import java.util.Map.Entry;

import org.eclipse.rwt.internal.theme.ThemePropertyAdapterRegistry.ThemePropertyAdapter;
import org.eclipse.rwt.internal.theme.css.ConditionalValue;


public final class ThemeStoreWriter {

  private final IThemeCssElement[] allThemeableWidgetElements;
  private final Collection<ThemeEntry> themes;

  public ThemeStoreWriter( IThemeCssElement[] elements ) {
    allThemeableWidgetElements = elements;
    themes = new ArrayList<ThemeEntry>();
  }

  public void addTheme( Theme theme, boolean isDefault ) {
    themes.add( new ThemeEntry( theme, isDefault ) );
  }

  public String createJs() {
    QxType[] allValues = getValuesFromAllThemes();
    Map valuesMap = createValuesMap( allValues );
    StringBuilder jsCode = new StringBuilder();
    jsCode.append( "( function( ts ) {\n" );
    jsCode.append( "ts.defineValues( " );
    jsCode.append( createJsonFromValuesMap( valuesMap ) );
    jsCode.append( " );\n" );
    Iterator iterator = themes.iterator();
    while( iterator.hasNext() ) {
      ThemeEntry themeEntry = ( ThemeEntry )iterator.next();
      jsCode.append( "ts.setThemeCssValues( " );
      jsCode.append( JsonValue.valueOf( themeEntry.theme.getJsId() ) );
      jsCode.append( ", " );
      jsCode.append( createThemeJson( themeEntry.theme ) );
      jsCode.append( ", " );
      jsCode.append( themeEntry.isDefault );
      jsCode.append( " );\n" );
    }
    jsCode.append( "} )( org.eclipse.swt.theme.ThemeStore.getInstance() );\n" );
    return jsCode.toString();
  }

  private JsonObject createThemeJson( Theme theme ) {
    JsonObject result = new JsonObject();
    ThemeCssValuesMap valuesMap = theme.getValuesMap();
    for( int i = 0; i < allThemeableWidgetElements.length; i++ ) {
      IThemeCssElement element = allThemeableWidgetElements[ i ];
      String elementName = element.getName();
      JsonObject elementObj = createThemeJsonForElement( valuesMap, element );
      result.append( elementName, elementObj );
    }
    return result;
  }

  private JsonObject createThemeJsonForElement( ThemeCssValuesMap valuesMap,
                                                IThemeCssElement element )
  {
    JsonObject result = new JsonObject();
    String[] properties = element.getProperties();
    ThemePropertyAdapterRegistry registry = ThemePropertyAdapterRegistry.getInstance();
    for( int i = 0; i < properties.length; i++ ) {
      String propertyName = properties[ i ];
      JsonArray valuesArray = new JsonArray();
      String elementName = element.getName();
      ConditionalValue[] values = valuesMap.getValues( elementName, propertyName );
      for( int j = 0; j < values.length; j++ ) {
        ConditionalValue conditionalValue = values[ j ];
        JsonArray array = new JsonArray();
        array.append( JsonArray.valueOf( conditionalValue.constraints ) );
        QxType value = conditionalValue.value;
        ThemePropertyAdapter adapter = registry.getPropertyAdapter( value.getClass() );
        String cssKey = adapter.getKey( value );
        array.append( cssKey );
        valuesArray.append( array );
      }
      result.append( propertyName, valuesArray );
    }
    return result;
  }

  private QxType[] getValuesFromAllThemes() {
    Set<QxType> valueSet = new LinkedHashSet<QxType>();
    Iterator iterator = themes.iterator();
    while( iterator.hasNext() ) {
      ThemeEntry themeEntry = ( ThemeEntry )iterator.next();
      Theme theme = themeEntry.theme;
      ThemeCssValuesMap valuesMap = theme.getValuesMap();
      QxType[] values = valuesMap.getAllValues();
      for( int i = 0; i < values.length; i++ ) {
        valueSet.add( values[ i ] );
      }
    }
    return valueSet.toArray( new QxType[ valueSet.size() ] );
  }

  private static Map createValuesMap( QxType[] values ) {
    Map<String,JsonObject> result = new LinkedHashMap<String,JsonObject>();
    for( int i = 0; i < values.length; i++ ) {
      appendValueToMap( values[ i ], result );
    }
    return result;
  }

  private static void appendValueToMap( QxType propertyValue, Map<String,JsonObject> valuesMap ) {
    ThemePropertyAdapterRegistry registry = ThemePropertyAdapterRegistry.getInstance();
    ThemePropertyAdapter adapter = registry.getPropertyAdapter( propertyValue.getClass() );
    if( adapter != null ) {
      String slot = adapter.getSlot( propertyValue );
      if( slot != null ) {
        String key = adapter.getKey( propertyValue );
        JsonValue value = adapter.getValue( propertyValue );
        if( value != null ) {
          JsonObject slotObject = getSlot( valuesMap, slot );
          slotObject.append( key, value );
        }
      }
    }
  }

  private static JsonValue createJsonFromValuesMap( Map valuesMap ) {
    JsonObject result = new JsonObject();
    Set entrySet = valuesMap.entrySet();
    Iterator keyIterator = entrySet.iterator();
    while( keyIterator.hasNext() ) {
      Entry entry = ( Entry )keyIterator.next();
      String key = ( String )entry.getKey();
      JsonValue value = ( JsonValue )entry.getValue();
      result.append( key, value );
    }
    return result;
  }

  private static JsonObject getSlot( Map<String,JsonObject> valuesMap, String name ) {
    JsonObject result = valuesMap.get( name );
    if( result == null ) {
      result = new JsonObject();
      valuesMap.put( name, result );
    }
    return result;
  }

  private static final class ThemeEntry {
  
    final Theme theme;
    final boolean isDefault;
  
    ThemeEntry( Theme theme, boolean isDefault ) {
      this.theme = theme;
      this.isDefault = isDefault;
    }
  }
}
