/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.browser.browserkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rwt.lifecycle.PhaseEvent;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.IServiceStore;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.widgets.Widget;


public final class BrowserLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Browser";
  private static final String[] ALLOWED_STYLES = new String[] { "BORDER" };

  static final String BLANK_HTML = "<html><script></script></html>";

  // Request parameters that denote ProgressEvents
  public static final String EVENT_PROGRESS_COMPLETED
    = "org.eclipse.swt.events.progressCompleted";

  private static final String PARAM_EXECUTE_RESULT = "executeResult";
  private static final String PARAM_EVALUATE_RESULT = "evaluateResult";
  static final String PARAM_EXECUTE_FUNCTION = "executeFunction";
  static final String PARAM_EXECUTE_ARGUMENTS = "executeArguments";
  private static final String PARAM_PROGRESS_LISTENER = "progress";
  private static final String PARAM_SCRIPT = "script";
  private static final String METHOD_EVALUATE = "evaluate";
  private static final String PARAM_FUNCTIONS = "functions";
  private static final String METHOD_CREATE_FUNCTIONS = "createFunctions";
  private static final String METHOD_DESTROY_FUNCTIONS = "destroyFunctions";
  private static final String PARAM_FUNCTION_RESULT = "functionResult";

  static final String EXECUTED_FUNCTION_NAME
    = Browser.class.getName() + "#executedFunctionName.";
  static final String EXECUTED_FUNCTION_RESULT
    = Browser.class.getName() + "#executedFunctionResult.";
  static final String EXECUTED_FUNCTION_ERROR
    = Browser.class.getName() + "#executedFunctionError.";
  private static final String FUNCTIONS_TO_CREATE
    = Browser.class.getName() + "#functionsToCreate.";
  private static final String FUNCTIONS_TO_DESTROY
    = Browser.class.getName() + "#functionsToDestroy.";


  @Override
  public void preserveValues( Widget widget ) {
    Browser browser = ( Browser )widget;
    ControlLCAUtil.preserveValues( browser );
    WidgetLCAUtil.preserveCustomVariant( browser );
    preserveListener( browser, PARAM_PROGRESS_LISTENER, ProgressEvent.hasListener( browser ) );
  }

  public void readData( Widget widget ) {
    Browser browser = ( Browser )widget;
    readExecuteResult( browser );
    executeFunction( browser );
    fireProgressEvent( browser );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( browser );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( browser.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( browser, ALLOWED_STYLES ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Browser browser = ( Browser )widget;
    ControlLCAUtil.renderChanges( browser );
    WidgetLCAUtil.renderCustomVariant( browser );
    destroyBrowserFunctions( browser );
    renderUrl( browser );
    createBrowserFunctions( browser );
    renderEvaluate( browser );
    renderFunctionResult( browser );
    renderListener( browser, PARAM_PROGRESS_LISTENER, ProgressEvent.hasListener( browser ), false );
  }

  @Override
  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }

  private static void fireProgressEvent( Browser browser ) {
    String fireProgressEvent = WidgetLCAUtil.readPropertyValue( browser, EVENT_PROGRESS_COMPLETED );
    if( fireProgressEvent != null ) {
      ProgressEvent changedEvent = new ProgressEvent( browser, ProgressEvent.CHANGED );
      changedEvent.processEvent();
      ProgressEvent completedEvent = new ProgressEvent( browser, ProgressEvent.COMPLETED );
      completedEvent.processEvent();
    }
  }

  private static void readExecuteResult( Browser browser ) {
    String executeValue = WidgetLCAUtil.readPropertyValue( browser, PARAM_EXECUTE_RESULT );
    if( executeValue != null ) {
      String evalValue = WidgetLCAUtil.readPropertyValue( browser, PARAM_EVALUATE_RESULT );
      boolean executeResult = Boolean.valueOf( executeValue ).booleanValue();
      Object evalResult = null;
      if( evalValue != null ) {
        Object[] parsedValues = parseArguments( evalValue );
        if( parsedValues.length == 1 ) {
          evalResult = parsedValues[ 0 ];
        }
      }
      browser.getAdapter( IBrowserAdapter.class ).setExecuteResult( executeResult, evalResult );
    }
  }

  private static void renderUrl( Browser browser ) throws IOException {
    if( hasUrlChanged( browser ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( browser );
      clientObject.set( "url", getUrl( browser ) );
      browser.getAdapter( IBrowserAdapter.class ).resetUrlChanged();
    }
  }

  static boolean hasUrlChanged( Browser browser ) {
    boolean initialized = WidgetUtil.getAdapter( browser ).isInitialized();
    return !initialized || browser.getAdapter( IBrowserAdapter.class ).hasUrlChanged();
  }

  static String getUrl( Browser browser ) throws IOException {
    String text = getText( browser );
    String url = browser.getUrl();
    String result;
    if( !"".equals( text.trim() ) ) {
      result = registerHtml( text );
    } else if( !"".equals( url.trim() ) ) {
      result = url;
    } else {
      result = registerHtml( BLANK_HTML );
    }
    return result;
  }

  private static void renderEvaluate( final Browser browser ) {
    IBrowserAdapter adapter = browser.getAdapter( IBrowserAdapter.class );
    final String executeScript = adapter.getExecuteScript();
    boolean executePending = adapter.getExecutePending();
    if( executeScript != null && !executePending ) {
      // [if] Put the execution to the end of the rendered script. This is very
      // important when Browser#execute is called from within a BrowserFunction,
      // because then we have a synchronous requests.
      RWTFactory.getLifeCycleFactory().getLifeCycle().addPhaseListener( new PhaseListener() {
        public void beforePhase( PhaseEvent event ) {
        }
        public void afterPhase( PhaseEvent event ) {
          if( browser.getDisplay() == LifeCycleUtil.getSessionDisplay() ) {
            try {
              IClientObject clientObject = ClientObjectFactory.getClientObject( browser );
              Map<String, Object> properties = new HashMap<String, Object>();
              properties.put( PARAM_SCRIPT, executeScript );
              clientObject.call( METHOD_EVALUATE, properties );
            } finally {
              RWTFactory.getLifeCycleFactory().getLifeCycle().removePhaseListener( this );
            }
          }
        }
        public PhaseId getPhaseId() {
          return PhaseId.RENDER;
        }
      } );
      adapter.setExecutePending( true );
    }
  }

  private static String registerHtml( String html ) throws IOException {
    String name = createUrlFromHtml( html );
    byte[] bytes = html.getBytes( "UTF-8" );
    InputStream inputStream = new ByteArrayInputStream( bytes );
    IResourceManager resourceManager = RWT.getResourceManager();
    resourceManager.register( name, inputStream );
    return resourceManager.getLocation( name );
  }

  private static String createUrlFromHtml( String html ) {
    StringBuilder result = new StringBuilder();
    result.append( "org.eclipse.swt.browser/text" );
    result.append( String.valueOf( html.hashCode() ) );
    result.append( ".html" );
    return result.toString();
  }

  private static String getText( Browser browser ) {
    Object adapter = browser.getAdapter( IBrowserAdapter.class );
    IBrowserAdapter browserAdapter = ( IBrowserAdapter )adapter;
    return browserAdapter.getText();
  }

  //////////////////////////////////////
  // Helping methods for BrowserFunction

  private static void createBrowserFunctions( Browser browser ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    String id = WidgetUtil.getId( browser );
    String[] functions = ( String[] )serviceStore.getAttribute( FUNCTIONS_TO_CREATE + id );
    if( functions != null ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( browser );
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( PARAM_FUNCTIONS, functions );
      clientObject.call( METHOD_CREATE_FUNCTIONS, properties );
    }
  }

  private static void destroyBrowserFunctions( Browser browser ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    String id = WidgetUtil.getId( browser );
    String[] functions = ( String[] )serviceStore.getAttribute( FUNCTIONS_TO_DESTROY + id );
    if( functions != null ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( browser );
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( PARAM_FUNCTIONS, functions );
      clientObject.call( METHOD_DESTROY_FUNCTIONS, properties );
    }
  }

  private static void executeFunction( final Browser browser ) {
    String function = WidgetLCAUtil.readPropertyValue( browser, PARAM_EXECUTE_FUNCTION );
    String arguments = WidgetLCAUtil.readPropertyValue( browser, PARAM_EXECUTE_ARGUMENTS );
    if( function != null ) {
      IBrowserAdapter adapter = browser.getAdapter( IBrowserAdapter.class );
      BrowserFunction[] functions = adapter.getBrowserFunctions();
      boolean found = false;
      for( int i = 0; i < functions.length && !found; i++ ) {
        final BrowserFunction current = functions[ i ];
        if( current.getName().equals( function ) ) {
          final Object[] args = parseArguments( arguments );
          ProcessActionRunner.add( new Runnable() {
            public void run() {
              try {
                Object executedFunctionResult = current.function( args );
                setExecutedFunctionResult( browser, executedFunctionResult );
              } catch( Exception e ) {
                setExecutedFunctionError( browser, e.getMessage() );
              }
              setExecutedFunctionName( browser, current.getName() );
            }
          } );
          found = true;
        }
      }
    }
  }

  private static void renderFunctionResult( Browser browser ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    String id = WidgetUtil.getId( browser );
    String name = ( String )serviceStore.getAttribute( EXECUTED_FUNCTION_NAME + id );
    if( name != null ) {
      Object result = serviceStore.getAttribute( EXECUTED_FUNCTION_RESULT + id );
      String error = ( String )serviceStore.getAttribute( EXECUTED_FUNCTION_ERROR + id );
      IClientObject clientObject = ClientObjectFactory.getClientObject( browser );
      Object[] value = new Object[] {
        name, result, error
      };
      clientObject.set( PARAM_FUNCTION_RESULT, value );
    }
  }

  private static void setExecutedFunctionName( Browser browser, String name ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    String id = WidgetUtil.getId( browser );
    serviceStore.setAttribute( EXECUTED_FUNCTION_NAME + id, name );
  }

  private static void setExecutedFunctionResult( Browser browser, Object result ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    String id = WidgetUtil.getId( browser );
    serviceStore.setAttribute( EXECUTED_FUNCTION_RESULT + id, result );
  }

  private static void setExecutedFunctionError( Browser browser, String error ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    String id = WidgetUtil.getId( browser );
    serviceStore.setAttribute( EXECUTED_FUNCTION_ERROR + id, error );
  }

  static Object[] parseArguments( String arguments ) {
    List<Object> result = new ArrayList<Object>();
    if( arguments.startsWith( "[" ) && arguments.endsWith( "]" ) ) {
      // remove [ ] brackets
      String args = arguments.substring( 1, arguments.length() - 1 );
      int openQuotes = 0;
      int openBrackets = 0;
      String arg;
      StringBuilder argBuff = new StringBuilder();
      char prevChar = ' ';
      for( int i = 0; i < args.length(); i++ ) {
        char ch = args.charAt( i );
        if( ch == ',' && openQuotes == 0 && openBrackets == 0 ) {
          arg = argBuff.toString();
          if( arg.startsWith( "[" ) ) {
            result.add( parseArguments( arg ) );
          } else {
            arg = arg.replaceAll( "\\\\\"", "\"" );
            result.add( withType( arg ) );
          }
          argBuff.setLength( 0 );
        } else {
          if( ch == '"' && prevChar != '\\' ) {
            if( openQuotes == 0 ) {
              openQuotes++;
            } else {
              openQuotes--;
            }
          } else if( ch == '[' && openQuotes == 0 ) {
            openBrackets++;
          } else if( ch == ']'&& openQuotes == 0 ) {
            openBrackets--;
          }
          argBuff.append( ch );
        }
        prevChar = ch;
      }
      // append last segment
      arg = argBuff.toString();
      if( arg.startsWith( "[" ) ) {
        result.add( parseArguments( arg ) );
      } else if( !arg.equals( "" ) ) {
        arg = arg.replaceAll( "\\\\\"", "\"" );
        result.add( withType( arg ) );
      }
    }
    return result.toArray();
  }

  static Object withType( String argument ) {
    Object result;
    if( argument.equals( "null" ) || argument.equals( "undefined" ) ) {
      result = null;
    } else if( argument.equals( "true" ) || argument.equals( "false" ) ) {
      result = new Boolean( argument );
    } else if( argument.startsWith( "\"" ) ) {
      result = argument.substring( 1, argument.length() - 1 );
    } else {
      try {
        result = Double.valueOf( argument );
      } catch( NumberFormatException nfe ) {
        result = argument;
      }
    }
    return result;
  }
}
