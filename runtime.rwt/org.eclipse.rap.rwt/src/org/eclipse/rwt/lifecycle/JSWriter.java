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
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.lifecycle.CurrentPhase;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


/**
 * This class provides helper methods to generate Javascript used to update the
 * client-side state of widgets.
 * <p>Note that the Javascript code that is rendered relies on the client-side
 * <code>org.eclipse.swt.WidgetManager</code> to be present.</p>
 *
 * @see AbstractWidgetLCA
 * @see ControlLCAUtil
 * @see WidgetLCAUtil
 *
 * @since 1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public final class JSWriter {

  /**
   * A reference to the current widget manager on the client side.
   *
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public static JSVar WIDGET_MANAGER_REF = new JSVar( "wm" );

  /**
   * Reference to the widget of this JSWriter instance.
   *
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed
   * from application code.
   * </p>
   */
  public static JSVar WIDGET_REF = new JSVar( "w" );

  private static final JSVar TARGET_REF = new JSVar( "t" );

  private static final String WRITER_MAP = JSWriter.class.getName() + "#map";
  private static final String HAS_WIDGET_MANAGER = JSWriter.class.getName() + "#hasWidgetManager";
  private static final String CURRENT_WIDGET_REF = JSWriter.class.getName() + "#currentWidgetRef";

  private static final Map<String,String> setterNames = new HashMap<String,String>();
  private static final Map<String,String> getterNames = new HashMap<String,String>();
  private static final Map<String,String> resetterNames = new HashMap<String,String>();


  private final Widget widget;

  /**
   * Returns an instance of {@link JSWriter} for the specified
   * <code>widget</code>. Only this writer can modify attributes and call
   * methods on the client-side representation of the widget.
   *
   * @param widget the widget for the requested {@link JSWriter}
   * @return the corresponding {@link JSWriter}
   */
  @SuppressWarnings("unchecked")
  public static JSWriter getWriterFor( Widget widget ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JSWriter result;
    Map<Widget,JSWriter> map = ( Map<Widget,JSWriter> )stateInfo.getAttribute( WRITER_MAP );
    if( map == null ) {
      map = new HashMap<Widget,JSWriter>();
      stateInfo.setAttribute( WRITER_MAP, map );
    }
    if( map.containsKey( widget ) ) {
      result = map.get( widget );
    } else {
      result = new JSWriter( widget );
      map.put( widget, result );
    }
    return result;
  }

  /**
   * Returns the {@link JSWriter} instance used to reset a widgets attributes in
   * order to take part in the pooling mechanism.
   *
   * @return the {@link JSWriter} instance
   * @deprecated As of 1.3, server-side widget pooling is no longer required.
   *             This method should not be used anymore.
   */
  public static JSWriter getWriterForResetHandler() {
    return new JSWriter( null );
  }

  private JSWriter( Widget widget ) {
    this.widget = widget;
  }

  /**
   * Creates a new widget on the client-side by creating an instance of the
   * corresponding javascript class defined by <code>className</code>. This
   * is normally done in the <code>renderInitialization</code> method of the
   * widgets life-cycle adapter (LCA).
   *
   * @param className the javascript class to initiate
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA#renderInitialization
   */
  public void newWidget( String className ) throws IOException {
    newWidget( className, null );
  }

  /**
   * Creates a new widget on the client-side by creating an instance of the
   * corresponding javascript class definition. This is normally done in
   * the <code>renderInitialization</code> method of the widgets life-cycle
   * adapter (LCA). All arguments passed to this function will be transmitted
   * to the client and used to call the constructor of the javascript widget.
   *
   * @param className the javascript class to initiate
   * @param args the arguments for the widgets constructor on the client-side
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA#renderInitialization
   */
  public void newWidget( String className, Object[] args )
    throws IOException
  {
    ensureWidgetManager();
    StringBuilder buffer = new StringBuilder();
    boolean isControl = widget instanceof Control;
    boolean isShell = widget instanceof Shell;
    buffer.append( "var w = new " + className );
    buffer.append( "(" + createParamList( " ", args, " ", false ) + ");" );
    buffer.append( "wm.add( w, " );
    buffer.append( "\"" + WidgetUtil.getId( widget ) + "\", " );
    buffer.append( isControl + " );" );
    write( buffer.toString() );
    setCurrentWidgetRef( widget );
    if( isControl && !isShell ) {
      setParent( getJSParentId( widget ) );
    } else if( isShell ) {
      call( "addToDocument", null );
    }
  }

  /**
   * Explicitly sets the parent of the client-side widget.
   *
   * @param parentId the widget id of the parent
   * @throws IOException if an I/O error occurs
   * @see WidgetUtil
   */
  public void setParent( String parentId ) throws IOException {
    call( WIDGET_MANAGER_REF, "setParent", new Object[] { widget, parentId } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   *
   * @param jsProperty the attribute to change
   * @param value the new value
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String jsProperty, String value )
    throws IOException
  {
    call( getSetterName( jsProperty ), new Object[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   *
   * @param jsProperty the attribute to change
   * @param value the new value
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String jsProperty, int value )
    throws IOException
  {
    set( jsProperty, new int[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   *
   * @param jsProperty the attribute to change
   * @param value the new value
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String jsProperty, float value )
    throws IOException
  {
    set( jsProperty, new float[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   *
   * @param jsProperty the attribute to change
   * @param value the new value
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String jsProperty, boolean value )
    throws IOException
  {
    set( jsProperty, new boolean[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   *
   * @param jsProperty the attribute to change
   * @param values the new values
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String jsProperty, int[] values )
    throws IOException
  {
    String functionName = getSetterName( jsProperty );
    Integer[] integers = new Integer[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      integers[ i ] = new Integer( values[ i ] );
    }
    call( widget, functionName, integers );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   *
   * @param jsProperty the attribute to change
   * @param values the new values
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String jsProperty, float[] values )
    throws IOException
  {
    String functionName = getSetterName( jsProperty );
    Float[] floats = new Float[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      floats[ i ] = new Float( values[ i ] );
    }
    call( widget, functionName, floats );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   *
   * @param jsProperty the attribute to change
   * @param values the new values
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String jsProperty, boolean[] values )
    throws IOException
  {
    ensureWidgetRef();
    String functionName = getSetterName( jsProperty );
    Boolean[] parameters = new Boolean[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      parameters[ i ] = Boolean.valueOf( values[ i ] );
    }
    call( widget, functionName, parameters );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   *
   * @param jsProperty the attribute to change
   * @param value the new value
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String jsProperty, Object value )
    throws IOException
  {
    set( jsProperty, new Object[] { value } );
  }

  /**
   * Sets the specified property of the client-side widget to a new value.
   *
   * @param jsProperty the attribute to change
   * @param values the new values
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String jsProperty, Object[] values )
    throws IOException
  {
    call( widget, getSetterName( jsProperty ), values );
  }


  /**
   * Sets the specified properties of the client-side widget to new values.
   *
   * @param jsPropertyChain the attributes to change
   * @param values the new values
   *
   * @throws IOException if an I/O error occurs
   * @see AbstractWidgetLCA
   */
  public void set( String[] jsPropertyChain, Object[] values )
    throws IOException
  {
    call( widget, createPropertyChain( jsPropertyChain, false ), values );
  }

  /**
   * Sets the specified <code>jsProperty</code> of the client-side widget to
   * the new value. Uses the specified <code>javaProperty</code> as a key
   * to obtain the preserved value and only sets the new value if it has
   * changed since it was preserved.
   *
   * @param javaProperty the key used to obtain the preserved value
   * @param jsProperty the client-side attribute to change
   * @param newValue the new values
   *
   * @return <code>true</code> if the new value differs from the preserved
   * value (meaning that JavaScript was written); <code>false</code> otherwise
   *
   * @throws IOException if an I/O error occurs
   *
   * @see AbstractWidgetLCA#preserveValues(Widget)
   */
  public boolean set( String javaProperty, String jsProperty, Object newValue )
    throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    boolean changed
      =  !adapter.isInitialized()
      || WidgetLCAUtil.hasChanged( widget, javaProperty, newValue );
    if( changed ) {
      set( jsProperty, newValue );
    }
    return changed;
  }

  /**
   * Sets the specified <code>jsProperty</code> of the client-side widget to
   * the new value. Uses the specified <code>javaProperty</code> as a key
   * to obtain the preserved value and only sets the new value if it has
   * changed since it was preserved.
   * <p>If the widget is rendered for the first time, there is no preserved
   * value present. In this case the <code>defValue</code> is taken into
   * account instead of the preserved value of the <code>javaProperty</code>.
   * Therefore, the default value must match the initial value of the attribute
   * of the client-side widget. If there is no constant initial client-side
   * value, resort the {@link #set(String,String,Object)}.</p>
   *
   * @param javaProperty the key used to obtain the preserved value
   * @param jsProperty the client-side attribute to change
   * @param newValue the new values
   * @param defValue the default value
   *
   * @return <code>true</code> if the new value differs from the preserved
   * value (meaning that JavaScript was written); <code>false</code> otherwise
   *
   * @throws IOException if an I/O error occurs
   *
   * @see AbstractWidgetLCA#preserveValues(Widget)
   */
  public boolean set( String javaProperty, String jsProperty, Object newValue, Object defValue )
    throws IOException
  {
    boolean changed
      = WidgetLCAUtil.hasChanged( widget, javaProperty, newValue, defValue );
    if( changed ) {
      set( jsProperty, new Object[] { newValue } );
    }
    return changed;
  }

  /**
   * Resets the specified javascript property to its initial value.
   *
   * @param jsProperty the javascript property to reset
   *
   * @throws IOException if an I/O error occurs
   */
  public void reset( String jsProperty ) throws IOException {
    call( widget, getResetterName( jsProperty ), null );
  }

  /**
   * Resets the specified javascript properties to their initial values.
   *
   * @param jsPropertyChain the javascript properties to reset
   *
   * @throws IOException if an I/O error occurs
   */
  public void reset( String[] jsPropertyChain ) throws IOException {
    call( widget, createPropertyChain( jsPropertyChain, true ), null );
  }

  /**
   * This will add a listener to an object specified by the property of
   * the widget. The listener has to be a javascript function which accepts
   * exact one parameter - an <code>qx.event.type.Event</code> object.
   *
   * @param property the property of the widget to what the listener should be added
   * @param eventType the type of the event
   * @param listener reference to the listener function
   *
   * @throws IOException if an I/O error occurs
   */
  public void addListener( String property, String eventType, String listener )
    throws IOException
  {
    ensureWidgetRef();
    if( property == null ) {
      // TODO [rh] HACK to allow 'instance' listener instead of static listener
      //      functions
      if( listener.startsWith( "this." ) ) {
        String thisListener = listener.substring( 5 );
        String code = "w.addEventListener( \"{0}\", w.{1}, w );";
        write( code, eventType, thisListener );
      } else {
        String code = "w.addEventListener( \"{0}\", {1} );";
        write( code, eventType, listener );
      }
    } else {
      String code = "w.{0}().addEventListener( \"{1}\", {2} );";
      write( code, getGetterName( property ), eventType, listener );
    }
  }

  /**
   * This will add a listener to the widget of this {@link JSWriter}. The
   * listener has to be a javascript function which accepts exact one
   * parameter - an <code>qx.event.type.Event</code> object.
   *
   * @param eventType the type of the event
   * @param listener reference to the listener function
   *
   * @throws IOException if an I/O error occurs
   */
  public void addListener( String eventType, String listener )
    throws IOException
  {
    addListener( null, eventType, listener );
  }


  public void updateListener( String property, JSListenerInfo info, String javaListener, boolean hasListeners )
    throws IOException
  {
    if( info.getJSListenerType() == JSListenerType.ACTION ) {
      updateActionListener( property, info, javaListener, hasListeners );
    } else {
      updateStateAndActionListener( property,
                                    info,
                                    javaListener,
                                    hasListeners );
    }
  }

  public void updateListener( JSListenerInfo info, String javaListener, boolean hasListeners )
    throws IOException
  {
    updateListener( null, info, javaListener, hasListeners );
  }

  public void removeListener( String eventType, String listener ) {
    removeListener( null, eventType, listener );
  }

  public void removeListener( String property, String eventType, String listener ) {
    ensureWidgetRef();
    if( property == null ) {
      // TODO [rh] HACK to allow 'instance' listener instead of static listener
      //      functions
      if( listener.startsWith( "this." ) ) {
        String thisListener = listener.substring( 5 );
        String code = "w.removeEventListener( \"{0}\", w.{1}, w );";
        write( code, eventType, thisListener );
      } else {
        String code = "w.removeEventListener( \"{0}\", {1} );";
        write( code, eventType, listener );
      }
    } else {
      String code = "w.{0}().removeEventListener( \"{1}\", {2} );";
      write( code, getGetterName( property ), eventType, listener );
    }
  }


  /**
   * Calls a specific function of the widget on the client-side.
   *
   * @param function the function name
   * @param args the arguments for the function
   *
   * @throws IOException if an I/O error occurs
   */
  public void call( String function, Object[] args )
    throws IOException
  {
    call( widget, function, args );
  }

  /**
   * Calls the specific <code>function</code> of the widget identified by
   * <code>target</code> on the client-side.
   *
   * @param target the widget on which the function should be called
   * @param function the function to be called
   * @param args the arguments for the function
   *
   * @throws IOException if an I/O error occurs
   */
  public void call( Widget target, String function, Object[] args ) throws IOException {
    ensureWidgetManager();
    JSVar refVariable;
    if( target == widget ) {
      ensureWidgetRef();
      refVariable = WIDGET_REF;
    } else {
      refVariable = TARGET_REF;
      writeVarAssignment( refVariable, createFindWidgetById( target ) );
    }
    String params = createParamList( args );
    writeCall( refVariable, function, params );
  }

  /**
   * Calls the specific <code>function</code> of the widget identified by
   * <code>target</code> on the client-side.
   *
   * @param target the widget on which the function should be called
   * @param function the function name
   * @param args the arguments for the function
   *
   * @throws IOException if an I/O error occurs
   */
  public void call( JSVar target, String function, Object[] args ) throws IOException {
    ensureWidgetManager();
    String params = createParamList( args );
    writeCall( target, function, params );
  }

  public void startCall( JSVar target, String function, Object[] args ) {
    ensureWidgetManager();
    String params = createParamList( " ", args, "", false );
    write( "{0}.{1}({2}", target, function, params );
  }

  public void endCall( Object[] args ) {
    write( createParamList( "", args, "", false )  );
    write( " );" );
  }

  /**
   * Calls the specified Javascript function with the given arguments.
   *
   * @param function the function name
   * @param args the arguments for the function
   *
   * @throws IOException if an I/O error occurs
   */
  // TODO [rh] should we name this method 'call' and make it a static method?
  public void callStatic( String function, Object[] args ) throws IOException {
    ensureWidgetManager();
    String params = createParamList( args );
    StringBuilder buffer = new StringBuilder();
    buffer.append( function );
    buffer.append( '(' );
    buffer.append( params );
    buffer.append( ");" );
    write( buffer.toString() );
  }

  public void callFieldAssignment( JSVar target, String field, String value ) {
    write( "{0}.{1} = {2};", target, field, value );
  }

  public void varAssignment( JSVar var, String method ) {
    ensureWidgetManager();
    ensureWidgetRef();
    String value = WIDGET_REF + "." + method + "()";
    writeVarAssignment( var, value );
  }

  /**
   * Dispose is used to dispose of the widget of this {@link JSWriter} on the
   * client side.
   *
   * @throws IOException if an I/O error occurs
   */
  public void dispose() throws IOException {
    ensureWidgetManager();
    String widgetId = WidgetUtil.getId( widget );
    if( widget instanceof Control ) {
      ControlLCAUtil.resetActivateListener( ( Control )widget );
    }
    call( WIDGET_MANAGER_REF, "dispose", new Object[] { widgetId } );
  }

  ////////////////////////////////////////////////////////////////
  // helping methods for client side listener addition and removal

  private void updateActionListener( String property,
                                     JSListenerInfo info,
                                     String javaListener,
                                     boolean hasListeners ) throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if( adapter.isInitialized() ) {
      Boolean hadListeners = ( Boolean )adapter.getPreserved( javaListener );
      if( hadListeners == null || Boolean.FALSE.equals( hadListeners ) ) {
        if( hasListeners ) {
          addListener( property, info.getEventType(), info.getJSListener() );
        }
      } else if( !hasListeners ) {
        removeListener( property, info.getEventType(), info.getJSListener() );
      }
    } else {
      if( hasListeners ) {
        addListener( property, info.getEventType(), info.getJSListener() );
      }
    }
  }

  private void updateStateAndActionListener( String property,
                                             JSListenerInfo info,
                                             String javaListener,
                                             boolean hasListeners ) throws IOException
  {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if( adapter.isInitialized() ) {
      Boolean hadListeners = ( Boolean )adapter.getPreserved( javaListener );
      if( hadListeners == null || Boolean.FALSE.equals( hadListeners ) ) {
        if( hasListeners ) {
          removeListener( property, info.getEventType(), info.getJSListener() );
          addListener( property,
                       info.getEventType(),
                       createJsActionListener( info ) );
        }
      } else {
        if( !hasListeners ) {
          removeListener( property,
                          info.getEventType(),
                          createJsActionListener( info ) );
          addListener( property, info.getEventType(), info.getJSListener() );
        }
      }
    } else {
      if( hasListeners ) {
        addListener( property,
                     info.getEventType(),
                     createJsActionListener( info ) );
      } else {
        addListener( property, info.getEventType(), info.getJSListener() );
      }
    }
  }

  private String createJsActionListener( JSListenerInfo info ) {
    StringBuilder buffer = new StringBuilder();
    buffer.append( info.getJSListener() );
    buffer.append( "Action" );
    return buffer.toString();
  }

  /////////////////////////////////////////////////////////////////////
  // Helping methods for JavaScript WidgetManager and Widget references

  private String getJSParentId( Widget widget ) {
    String result = "";
    if( !( widget instanceof Shell ) && widget instanceof Control ) {
      Control control = ( Control )widget;
      WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( control );
      if( adapter.getJSParent() == null ) {
        result = WidgetUtil.getId( control.getParent() );
      } else {
        result = adapter.getJSParent();
      }
    }
    return result;
  }

  private void ensureWidgetManager() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    if(    currentPhaseIsRender()
        && widget != null
        && stateInfo.getAttribute( HAS_WIDGET_MANAGER ) == null )
    {
      writeVarAssignment( WIDGET_MANAGER_REF, "org.eclipse.swt.WidgetManager.getInstance()" );
      stateInfo.setAttribute( HAS_WIDGET_MANAGER, Boolean.TRUE );
    }
  }

  // TODO [fappel]: FontSizeCalculation causes problems with widget manager
  //                in IE. See FontSizeCalculationHandler#createFontParam.
  //                Until a better solution is found this hack is needed.
  private boolean currentPhaseIsRender() {
    return CurrentPhase.get() != null
        && CurrentPhase.get() != PhaseId.PROCESS_ACTION
        && CurrentPhase.get() != PhaseId.PREPARE_UI_ROOT
        && CurrentPhase.get() != PhaseId.READ_DATA;
  }

  private void ensureWidgetRef() {
    ensureWidgetManager();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    Object currentWidgetRef = stateInfo.getAttribute( CURRENT_WIDGET_REF );
    if( widget != currentWidgetRef && widget != null ) {
      writeVarAssignment( WIDGET_REF, createFindWidgetById( widget ) );
      setCurrentWidgetRef( widget );
    }
  }

  private static void setCurrentWidgetRef( Widget widget ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setAttribute( CURRENT_WIDGET_REF, widget );
  }

  private static Widget getCurrentWidgetRef() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return ( Widget )stateInfo.getAttribute( CURRENT_WIDGET_REF );
  }

  private static String createFindWidgetById( Widget widget ) {
    return createFindWidgetById( WidgetUtil.getId( widget ) );
  }

  private static String createFindWidgetById( String id ) {
    StringBuilder buffer = new StringBuilder();
    buffer.append( WIDGET_MANAGER_REF.toString() );
    buffer.append( ".findWidgetById( \"" );
    buffer.append( id );
    buffer.append( "\" )" );
    return buffer.toString();
  }

  ///////////////////////////////////////////////
  // Helping methods to construct parameter lists

  private static String createParamList( Object[] args ) {
    return createParamList( " ", args, " ", true );
  }

  private static String createParamList( String startList,
                                         Object[] args,
                                         String endList,
                                         boolean useCurrentWidgetRef )
  {
    StringBuilder params = new StringBuilder();
    if( args != null ) {
      for( int i = 0; i < args.length; i++ ) {
        if( i == 0 ) {
          params.append( startList );
        }
        if( args[ i ] instanceof String ) {
          params.append( '"' );
          params.append( escapeString( ( String )args[ i ] ) );
          params.append( '"' );
        } else if( args[ i ] instanceof Character ) {
          params.append( '"' );
          params.append( args[ i ] );
          params.append( '"' );
        } else if( args[ i ] instanceof Widget ) {
          if( useCurrentWidgetRef && args[ i ] == getCurrentWidgetRef() ) {
            params.append( "w" );
          } else {
            params.append( createFindWidgetById( ( Widget )args[ i ] ) );
          }
        } else if( args[ i ] instanceof JSVar ) {
          params.append( args[ i ] );
        } else if( args[ i ] instanceof Color ) {
          params.append( '"' );
          params.append( getColorValue( ( Color )args[ i ] ) );
          params.append( '"' );
        } else if( args[ i ] instanceof RGB ) {
          params.append( '"' );
          params.append( getColorValue( ( RGB )args[ i ] ) );
          params.append( '"' );
        } else if( args[ i ] instanceof Object[] ) {
          params.append( createArray( ( Object[] )args[ i ] ) );
        } else {
          params.append( args[ i ] );
        }
        if( i == args.length - 1 ) {
          params.append( endList );
        } else {
          params.append( ", " );
        }
      }
    }
    return params.toString();
  }

  private static String createArray( Object[] array ) {
    StringBuilder buffer = new StringBuilder();
    buffer.append( '[' );
    for( int i = 0; i < array.length; i++ ) {
      if( i > 0 ) {
        buffer.append( "," );
      }
      if( array[ i ] instanceof String ) {
        buffer.append( " \"" );
        buffer.append( escapeString( array[ i ].toString() ) );
        buffer.append( '"' );
      } else if( array[ i ] instanceof Widget ) {
        buffer.append( createFindWidgetById( ( Widget )array[ i ] ) );
      } else if( array[ i ] instanceof Color ) {
        buffer.append( '"' );
        buffer.append( getColorValue( ( Color )array[ i ] ) );
        buffer.append( '"' );
      } else {
        buffer.append( array[ i ] );
      }
      if( i == array.length - 1 ) {
        buffer.append( ' ' );
      }
    }
    buffer.append( ']' );
    return buffer.toString();
  }

  private String createPropertyChain( String[] jsPropertyChain, boolean forReset ) {
    StringBuilder buffer = new StringBuilder();
    int last = jsPropertyChain.length - 1;
    for( int i = 0; i < last; i++ ) {
      buffer.append( getGetterName( jsPropertyChain[ i ] ) );
      buffer.append( "()." );
    }
    if( forReset ) {
      buffer.append( getResetterName( jsPropertyChain[ last ] ) );
    } else {
      buffer.append( getSetterName( jsPropertyChain[ last ] ) );
    }
    return buffer.toString();
  }

  private static Object getColorValue( Color color ) {
    return getColorValue( color.getRGB() );
  }

  private static String getColorValue( RGB rgb ) {
    StringBuilder buffer = new StringBuilder();
    buffer.append( "#" );
    String red = Integer.toHexString( rgb.red );
    if( red.length() == 1  ) {
      buffer.append( "0" );
    }
    buffer.append( red );
    String green = Integer.toHexString( rgb.green );
    if( green.length() == 1  ) {
      buffer.append( "0" );
    }
    buffer.append( green );
    String blue = Integer.toHexString( rgb.blue );
    if( blue.length() == 1  ) {
      buffer.append( "0" );
    }
    buffer.append( blue );
    return buffer.toString();
  }

  ////////////////////////////////////////
  // Helping methods to manipulate strings

  private static String capitalize( String text ) {
    String result;
    if( Character.isUpperCase( text.charAt( 0 ) ) ) {
      result = text;
    } else {
      StringBuilder buffer = new StringBuilder( text );
      char firstLetter = buffer.charAt( 0 );
      firstLetter = Character.toUpperCase( firstLetter );
      buffer.setCharAt( 0, firstLetter );
      result = buffer.toString();
    }
    return result;
  }

  // TODO [rh] revise how to handle newline characters (\n)
  private static String escapeString( String input ) {
    String result = EncodingUtil.escapeDoubleQuoted( input );
    return EncodingUtil.replaceNewLines( result );
  }

  private static String getSetterName( String jsProperty ) {
    synchronized( setterNames ) {
      String result = setterNames.get( jsProperty );
      if( result == null ) {
        StringBuilder functionName = new StringBuilder();
        functionName.append( "set" );
        functionName.append( capitalize( jsProperty ) );
        result =  functionName.toString();
        setterNames.put( jsProperty, result );
      }
      return result;
    }
  }

  private static String getResetterName( String jsProperty ) {
    synchronized( resetterNames ) {
      String result = resetterNames.get( jsProperty );
      if( result == null ) {
        StringBuilder functionName = new StringBuilder();
        functionName.append( "reset" );
        functionName.append( capitalize( jsProperty ) );
        result = functionName.toString();
        resetterNames.put( jsProperty, result );
      }
      return result;
    }
  }

  private static String getGetterName( String jsProperty ) {
    synchronized( getterNames ) {
      String result = getterNames.get( jsProperty );
      if( result == null ) {
        StringBuilder functionName = new StringBuilder();
        functionName.append( "get" );
        functionName.append( capitalize( jsProperty ) );
        result = functionName.toString();
        getterNames.put( jsProperty, result );
      }
      return result;
    }
  }

  /////////////////////////////////////////////////////////
  // Helping methods to write to the actual response writer

  private void writeCall( JSVar target, String function, String params ) {
    StringBuilder buffer = new StringBuilder();
    buffer.append( target.toString() );
    buffer.append( '.' );
    buffer.append( function );
    buffer.append( '(' );
    buffer.append( params );
    buffer.append( ");" );
    write( buffer.toString() );
  }

  private void writeVarAssignment( JSVar var, String value ) {
    StringBuilder buffer = new StringBuilder();
    buffer.append( "var " );
    buffer.append( var.toString() );
    buffer.append( " = " );
    buffer.append( value );
    buffer.append( ';' );
    write( buffer.toString() );
  }

  private static String format( String pattern, Object[] arguments ) {
    return MessageFormat.format( pattern, arguments );
  }

  private static void write( String pattern, Object arg1, Object arg2 ) {
    Object[] args = new Object[] { arg1, arg2 };
    write( format( pattern, args ) );
  }

  private static void write( String pattern, Object arg1, Object arg2, Object arg3 ) {
    Object[] args = new Object[] { arg1, arg2, arg3 };
    write( format( pattern, args ) );
  }

  private static void write( String code ) {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    ProtocolMessageWriter protocolWriter = stateInfo.getProtocolWriter();
    // HACK [rst] ProtocolMessageWriter clears these state info attributes
    // whenever a new operation is created. But this also happens when we call
    // ProtocolMessageWriter#appendExecuteScript() here, immediately resetting
    // the values set in the ensureXxx() methods above.
    // Thus we buffer the values here ...
    Object bufferHasWidgetManager = stateInfo.getAttribute( HAS_WIDGET_MANAGER );
    Object bufferCurrentWidgetRef = stateInfo.getAttribute( CURRENT_WIDGET_REF );
    // ... append the execute operation ...
    protocolWriter.appendExecuteScript( "jsex", "text/javascript", code.trim() );
    // ... and re-install them afterwards.
    if( bufferHasWidgetManager != null ) {
      stateInfo.setAttribute( HAS_WIDGET_MANAGER, bufferHasWidgetManager );
    }
    if( bufferCurrentWidgetRef != null ) {
      stateInfo.setAttribute( CURRENT_WIDGET_REF, bufferCurrentWidgetRef );
    }
  }

}
