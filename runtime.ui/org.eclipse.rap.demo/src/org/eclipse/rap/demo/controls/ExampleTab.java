/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.demo.controls;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;


@SuppressWarnings("restriction")
abstract class ExampleTab implements Serializable {

  private boolean contentCreated;
  private final CTabFolder folder;
  protected final List<Control> controls;

  private Composite exmplComp;
  protected Composite styleComp;
  protected Color[] bgColors;
  protected Color[] fgColors;
  private Font font;
  private int fgIndex;
  private int bgIndex;
  private int rbIndex;
  private boolean showBgImage = false;
  private boolean showBgGradient = false;

  private boolean visible = true;
  private boolean enabled = true;
  private Text text;
  private final StringBuffer content = new StringBuffer();
  private FontDialog fontChooser;
  private ColorChooser fgColorChooser;
  private ColorChooser bgColorChooser;
  private int defaultStyle = SWT.NONE;
  private final CTabItem item;
  private final Set<String> properties = new HashSet<String>();

  public static final Color BG_COLOR_GREEN = Graphics.getColor( 154, 205, 50 );
  public static final Color BG_COLOR_BLUE = Graphics.getColor( 105, 89, 205 );
  public static final Color BG_COLOR_BROWN = Graphics.getColor( 192, 172, 137 );
  public static final Color FG_COLOR_RED = Graphics.getColor( 194, 0, 23 );
  public static final Color FG_COLOR_BLUE = Graphics.getColor( 28, 96, 141 );
  public static final Color FG_COLOR_ORANGE = Graphics.getColor( 249, 158, 0 );

  public static final Color BGG_COLOR_GREEN = Graphics.getColor( 0, 255, 0 );
  public static final Color BGG_COLOR_BLUE = Graphics.getColor( 0, 0, 255 );

  private static final String[] SWT_CURSORS = {
    "null",
    "CURSOR_ARROW",
    "CURSOR_WAIT",
    "CURSOR_APPSTARTING",
    "CURSOR_CROSS",
    "CURSOR_HELP",
    "CURSOR_NO",
    "CURSOR_SIZEALL",
    "CURSOR_SIZENESW",
    "CURSOR_SIZENS",
    "CURSOR_SIZENWSE",
    "CURSOR_SIZEWE",
    "CURSOR_SIZEN",
    "CURSOR_SIZES",
    "CURSOR_SIZEE",
    "CURSOR_SIZEW",
    "CURSOR_SIZENE",
    "CURSOR_SIZESE",
    "CURSOR_SIZESW",
    "CURSOR_SIZENW",
    "CURSOR_IBEAM",
    "CURSOR_HAND",
    "CURSOR_UPARROW"
  };

  public ExampleTab( final CTabFolder parent, final String title ) {
    folder = parent;
    controls = new ArrayList<Control>();
    item = new CTabItem( folder, SWT.NONE );
    item.setText( title + " " );
  }

  public void createContents() {
    if( !contentCreated ) {
      Control sashForm = createSashForm();
      item.setControl( sashForm );
      initColors();
      createExampleControls( exmplComp );
      createStyleControls( styleComp );
      exmplComp.layout();
      styleComp.layout();
      contentCreated = true;
    }
  }

  protected void createNew() {
    controls.clear();
    destroyExampleControls();
    createExampleControls( exmplComp );
    updateVisible();
    updateEnabled();
    if( fgColorChooser != null ) {
      updateFgColor();
    }
    if( bgColorChooser != null ) {
      updateBgColor();
    }
    updateBgImage();
    updateBgGradient();
    if( fontChooser != null ) {
      // Control control = ( Control )controls.get( 0 );
      // font = control.getFont();
      // if( font != null ) {
      //   fontChooser.setFont( font );
      // }
      updateFont();
    }
    exmplComp.layout();
  }

  private Control createSashForm() {
    SashForm vertSashForm = new SashForm( folder, SWT.VERTICAL );
    SashForm horSashForm = new SashForm( vertSashForm, SWT.HORIZONTAL );
    Composite leftComp = new Composite( horSashForm, SWT.NONE );
    Composite rightComp = new Composite( horSashForm, SWT.NONE );
    Composite footComp = new Composite( vertSashForm, SWT.NONE );
    createLeft( leftComp );
    createRight( rightComp );
    createFoot( footComp );
    horSashForm.setWeights( new int[] { 60, 40 } );
    vertSashForm.setWeights( new int[] { 95, 5 } );
    return vertSashForm;
  }

  private void createLeft( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    Group exmplGroup = new Group( parent, SWT.NONE );
    exmplGroup.setLayout( new FillLayout() );
    exmplComp = new Composite( exmplGroup, SWT.NONE );
  }

  private void createRight( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    Group styleGroup = new Group( parent, SWT.NONE );
    styleGroup.setText( "Styles and Parameters" );
    styleGroup.setLayout( new FillLayout() );
    styleComp = new Composite( styleGroup, SWT.NONE );
    styleComp.setLayout( new RowLayout( SWT.VERTICAL ) );
  }

  private void createFoot( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    text = new Text( parent, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI );
    text.setText( "---" );
  }

  private void initColors() {
    bgColors = new Color[] {
      null,
      BG_COLOR_GREEN,
      BG_COLOR_BLUE,
      BG_COLOR_BROWN
    };
    fgColors = new Color[] {
      null,
      FG_COLOR_RED,
      FG_COLOR_BLUE,
      FG_COLOR_ORANGE
    };
  }

  protected abstract void createStyleControls( final Composite parent);

  protected abstract void createExampleControls( final Composite parent );

  /**
   * TODO [rst] Refactor ExampleTab to evaluate style controls before example
   *      controls are created.
   */
  protected void setDefaultStyle( final int style ) {
    defaultStyle = style;
  }

  protected Button createStyleButton( final String fieldName,
                                      final int style )
  {
    return createStyleButton( fieldName, style, false );
  }

  protected Button createStyleButton( final String name,
                                      final int style,
                                      final boolean checked ) {
    Button button = new Button( styleComp, SWT.CHECK );
    button.setText( name );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        createNew();
      }
    } );
    button.setData( "style", new Integer( style ) );
    button.setSelection( checked );
    return button;
  }

  protected Button createPropertyButton( final String text ) {
    return createPropertyButton( text, SWT.CHECK );
  }

  protected Button createPropertyButton( final String text, final int style ) {
    Button button = new Button( styleComp, style );
    button.setText( text );
    return button;
  }

  protected Button createPropertyCheckbox( final String text,
                                           final String prop )
  {
    return createPropertyCheckbox( text, prop, false );
  }

  protected Button createPropertyCheckbox( final String text,
                                           final String prop,
                                           final boolean checked )
  {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( text );
    button.setSelection( checked );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        if( button.getSelection() ) {
          properties.add( prop );
        } else {
          properties.remove( prop );
        }
        createNew();
      }
    } );
    return button;
  }

  public final boolean hasCreateProperty( final String name ) {
    return properties.contains( name );
  }

  /**
   * Creates a checkbutton to show / hide the registered controls.
   *
   * @return the created checkbutton
   */
  protected Button createVisibilityButton( ) {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Visible" );
    button.setSelection( visible );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        visible = button.getSelection();
        updateVisible();
      }
    } );
    return button;
  }

  /**
   * Creates a checkbutton to enable / disabled the registered controls.
   *
   * @return the created checkbutton.
   */
  protected Button createEnablementButton( ) {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Enabled" );
    button.setSelection( enabled );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        enabled = button.getSelection();
        updateEnabled();
      }
    } );
    return button;
  }

  /**
   * Creates a button to change the foreground color of all registered
   * controls.
   *
   * @return the created button
   */
  protected Button createFgColorButton() {
    fgColorChooser = new ColorChooser();
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Foreground" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        fgIndex = ( fgIndex + 1 ) % fgColors.length;
        updateFgColor();
      }

    } );
    return button;
  }

  /**
   * Creates a button to change the background color of all registered
   * controls.
   *
   * @return the created button
   */
  protected Button createBgColorButton() {
    bgColorChooser = new ColorChooser();
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Background" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        bgIndex = ( bgIndex + 1 ) % fgColors.length;
        updateBgColor();
      }
    } );
    return button;
  }

  /**
   * Creates a button to change the background gradient of all registered
   * controls.
   *
   * @return the created button
   */
  protected Button createBgGradientButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Background Gradient" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showBgGradient = button.getSelection();
        updateBgGradient();
      }
    } );
    return button;
  }

  /**
   * Creates a checkbox that controls whether a background image is set on the
   * registered controls.
   *
   * @return the created checkbox
   */
  protected Button createBgImageButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Background Image" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showBgImage = button.getSelection();
        updateBgImage();
      }
    } );
    return button;
  }

  protected Button createFontChooser() {
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Font" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        fontChooser = new FontDialog( getShell(), SWT.NONE );
        Control control = controls.get( 0 );
        fontChooser.setFontList( control.getFont().getFontData() );
        if( fontChooser.open() != null ) {
          font = new Font( control.getDisplay(), fontChooser.getFontList() );
        }
        updateFont();
      }
    } );
    return button;
  }

  /**
   * Experimental. Switching themes at runtime does not yet work properly.
   */
  protected void createThemeSwitcher( final Composite parent ) {
    final Button button = new Button( parent, SWT.PUSH );
    button.setText( "Theme Switcher" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Shell shell = new Shell( parent.getShell(), SWT.DIALOG_TRIM );
        shell.setText( "Theme Switcher" );
        shell.setLayout( new GridLayout() );
        Button themeButton = new Button( shell, SWT.PUSH );
        themeButton.setText( "Switch Theme" );
        themeButton.addSelectionListener( new SelectionAdapter() {
          String[] availableThemeIds = ThemeUtil.getAvailableThemeIds();
          @Override
          public void widgetSelected( final SelectionEvent e ) {
            int index = 0;
            String currThemeId = ThemeUtil.getCurrentThemeId();
            for( int i = 0; i < availableThemeIds.length; i++ ) {
              if( currThemeId.equals( availableThemeIds[ i ] ) ) {
                index = ( i + 1 ) % availableThemeIds.length;
              }
            }
            String newThemeId = availableThemeIds[ index ];
            ThemeUtil.setCurrentThemeId( newThemeId );
          }
        } );
        shell.pack();
        shell.open();
      }
    } );
  }

  /**
   * Creates a combo that controls whether a cursor is set on the
   * registered controls.
   *
   * @return the created combo
   */
  protected Combo createCursorCombo() {
    Composite group = new Composite( styleComp, SWT.NONE );
    group.setLayout( new GridLayout( 2, false ) );
    new Label( group, SWT.NONE ).setText( "Cursor" );
    final Combo combo = new Combo( group, SWT.READ_ONLY );
    combo.setItems( SWT_CURSORS );
    combo.select( 0 );
    combo.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( final SelectionEvent e ) {
        String selection = null;
        int index = combo.getSelectionIndex();
        if( index > 0 ) {
          selection = combo.getItem( index );
        }
        updateCursor( selection );
      }
    } );
    return combo;
  }

  /**
   * Creates a text that controls whether a border radius is set on the
   * registered controls.
   */
  protected void cteateRoundedBorderGroup() {
    Group group = new Group( styleComp, SWT.NONE );
    group.setText( "Rounded Border" );
    group.setLayout( new GridLayout( 2, false ) );
    new Label( group, SWT.NONE ).setText( "Width" );
    final Text textWidth = new Text( group, SWT.SINGLE | SWT.BORDER );
    textWidth.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    new Label( group, SWT.NONE ).setText( "Color" );
    final Button buttonColor = new Button( group, SWT.PUSH );
    buttonColor.setLayoutData( new GridData( 20, 20 ) );
    buttonColor.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        rbIndex = ( rbIndex + 1 ) % bgColors.length;
        if( bgColors[ rbIndex ] == null ) {
          buttonColor.setText( "" );
        } else {
          buttonColor.setText( "\u2588" );
        }
        buttonColor.setForeground( bgColors[ rbIndex ] );
      }
    } );
    new Label( group, SWT.NONE ).setText( "Radius " );
    Composite radiusGroup = new Composite( group, SWT.NONE );
    radiusGroup.setLayout( new GridLayout( 4, false ) );
    new Label( radiusGroup, SWT.NONE ).setText( "T-L" );
    final Text textTopLeft = new Text( radiusGroup, SWT.SINGLE | SWT.BORDER );
    textTopLeft.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    new Label( radiusGroup, SWT.NONE ).setText( "T-R" );
    final Text textTopRight = new Text( radiusGroup, SWT.SINGLE | SWT.BORDER );
    textTopRight.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    new Label( radiusGroup, SWT.NONE ).setText( "B-L" );
    final Text textBottomLeft = new Text( radiusGroup, SWT.SINGLE | SWT.BORDER );
    textBottomLeft.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    new Label( radiusGroup, SWT.NONE ).setText( "B-R" );
    final Text textBottomRight = new Text( radiusGroup, SWT.SINGLE | SWT.BORDER );
    textBottomRight.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    Button button = new Button( group, SWT.PUSH );
    button.setText( "Set" );
    button.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( final SelectionEvent e ) {
        int width = parseInt( textWidth.getText() );
        Color color = buttonColor.getForeground();
        int topLeft = parseInt( textTopLeft.getText() );
        int topRight = parseInt( textTopRight.getText() );
        int bottomRight = parseInt( textBottomRight.getText() );
        int bottomLeft = parseInt( textBottomLeft.getText() );
        updateRoundedBorder( width,
                             color,
                             topLeft,
                             topRight,
                             bottomRight,
                             bottomLeft );
      }
    } );
  }

  /**
   * Adds a control to the list of registered controls. Registered controls can
   * be hidden and disabled by the checkbuttons in the property area. This
   * method is to be called within <code>createExampleControls</code>.
   *
   * @param control A control that should be remote controlled.
   */
  protected void registerControl( final Control control ) {
    controls.add( control );
  }

  protected void log( final String msg ) {
    content.insert( 0, msg.trim() + text.getLineDelimiter() );
    text.setText( content.toString() );
  }

  protected Image loadImage( String name ) {
    InputStream stream = getClass().getClassLoader().getResourceAsStream( name );
    Image result = new Image( folder.getDisplay(), stream );
    try {
      stream.close();
    } catch( IOException ioe ) {
      throw new RuntimeException( ioe );
    }
    return result;
  }

  private void destroyExampleControls() {
    Control[] controls = exmplComp.getChildren();
    for( int i = 0; i < controls.length; i++ ) {
      controls[ i ].dispose();
    }
  }

  protected int getStyle() {
    int result = SWT.NONE;
    Control[] ctrls = styleComp.getChildren();
    if( ctrls.length == 0 ) {
      result = defaultStyle;
    } else {
      for( int i = 0; i < ctrls.length; i++ ) {
        if( ctrls[ i ] instanceof Button ) {
          Button button = ( Button )ctrls[ i ];
          if( button.getSelection() ) {
            Object data = button.getData( "style" );
            if( data instanceof Integer ) {
              int style = ( ( Integer )data ).intValue();
              result |= style;
            }
          }
        }
      }
    }
    return result;
  }

  private void updateVisible( ) {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setVisible( visible );
    }
  }

  private void updateEnabled( ) {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setEnabled( enabled );
    }
  }

  private void updateFgColor() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setForeground( fgColors[ fgIndex ] );
    }
  }

  private void updateBgColor() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setBackground( bgColors[ bgIndex ] );
    }
  }

  private void updateBgGradient() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
      IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
      if( showBgGradient ) {
        Color[] gradientColors = new Color[] {
          BGG_COLOR_BLUE,
          BGG_COLOR_GREEN,
          BGG_COLOR_BLUE,
          BGG_COLOR_GREEN,
          BGG_COLOR_BLUE
        };
        int[] percents = new int[] { 0, 25, 50, 75, 100 };
        gfxAdapter.setBackgroundGradient( gradientColors, percents, true );
      } else {
        gfxAdapter.setBackgroundGradient( null, null, true );
      }
    }
  }

  private void updateBgImage() {
    for( Control control : controls ) {
      Image image = Util.loadImage( control.getDisplay(), "resources/pattern.png" );
      control.setBackgroundImage( showBgImage ? image : null );
    }
  }

  private void updateFont() {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setFont( font );
    }
    // Force layout
    if( controls.size() > 0 ) {
      Composite parent = controls.get( 0 ).getParent();
      parent.layout( true, true );
    }
  }

  private void updateCursor( final String selection ) {
    Cursor cursor = null;
    Class swtClass = SWT.class;
    if( selection != null ) {
      try {
        Field field = swtClass.getField( selection );
        int cursorStyle = field.getInt( swtClass );
        cursor = Display.getCurrent().getSystemCursor( cursorStyle );
      } catch( Exception e ) {
        e.printStackTrace();
      }
    }
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      control.setCursor( cursor );
    }
  }

  private void updateRoundedBorder( final int width,
                                    final Color color,
                                    final int topLeft,
                                    final int topRight,
                                    final int bottomRight,
                                    final int bottomLeft ) {
    Iterator iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = ( Control )iter.next();
      Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
      IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
      gfxAdapter.setRoundedBorder( width,
                                   color,
                                   topLeft,
                                   topRight,
                                   bottomRight,
                                   bottomLeft );
    }
  }

  private int parseInt( final String text ) {
    int result;
    try {
      result = Integer.parseInt( text );
    } catch( NumberFormatException e ) {
      result = -1;
    }
    return result;
  }

  protected Shell getShell() {
    return folder.getShell();
  }
}
