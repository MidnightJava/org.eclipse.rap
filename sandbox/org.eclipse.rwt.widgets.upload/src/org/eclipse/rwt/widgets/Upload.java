/*******************************************************************************
 * Copyright (c) 2002-2007 Critical Software S.A. and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: Tiago Rodrigues (Critical Software S.A.) - initial implementation 
 *               Joel Oliveira (Critical Software S.A.) - initial commit
 *               Austin Riddle (Texas Center for Applied Technology) - migration to new FileUpload 
 *                  implementation
 ******************************************************************************/
package org.eclipse.rwt.widgets;

import java.io.*;

import org.eclipse.rap.rwt.supplemental.fileupload.*;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Widget representing an Upload box.
 *
 * @author tjarodrigues
 * @author stefan.roeck
 * @deprecated Use FileDialog or FileUpload implementation instead.
 */
public class Upload extends Composite {
  /**
   * Displays a progress bar inside the widget.
   */
  public final static int SHOW_PROGRESS = 1;

  /**
   * Fires progress events to registered UploadListeners.
   * If this flag is not set, only the {@link UploadListener#uploadFinished()}
   * event is fired.
   * @see UploadListener#uploadInProgress(UploadEvent)
   */
  public final static int FIRE_PROGRESS_EVENTS = 4;
  
  /**
   * Displays a upload button next to the browse button.
   */
  public final static int SHOW_UPLOAD_BUTTON = 2;

  private int flags;
  private boolean[] uploadInProgresses = { false };
  private FileUploadHandler handler;
  private InternalUploadListener listener;
  private Text fileText;
  private FileUpload browseBtn;
  private Button uploadBtn;
  private ProgressBar progressBar;
  private String processId;
  private File uploadedFile;
  private String uploadContentType;

  /**
   * Constructs a upload widget.
   * @param style Supported styles:
   * {@link SWT#BORDER}
   * @param flags supported flags:
   * {@link Upload#SHOW_PROGRESS}
   * {@link Upload#SHOW_UPLOAD_BUTTON}
   * {@link Upload#FIRE_PROGRESS_EVENTS}
   * The SHOW_PROGRESS flag implies the flag FIRE_PROGRESS_EVENTS.
   */
  public Upload( final Composite parent,
                 final int style,
                 final int flags )
  {
    super( parent, style );
    this.flags = flags;

    if ((this.flags & SHOW_PROGRESS) > 0) {
      this.flags |= FIRE_PROGRESS_EVENTS;
    }
    initHandler();
    createChildren();
  }

  /**
   * Convenience constructor for creating an upload widget without upload
   * button and progress bar. Same as {@link Upload(parent,int,int)} with 0 as
   * value for the flags parameter.
   */
  public Upload( final Composite parent,
                 final int style )
  {
    this( parent, style, 0 );
  }

  /**
   * Returns the full file name of the last
   * uploaded file including the file path as
   * selected by the user on his local machine.
   * <br>
   * The full path including the directory and file
   * drive are only returned, if the browser supports
   * reading this properties. In Firefox 3, only
   * the filename is returned.
   * @see Upload#getLastFileUploaded()
   */
  public String getPath() {
    checkWidget();
    return browseBtn.getFileName();
  }

  /**
   * Triggers a file upload. This method immediately returns, if the user hasn't
   * selected a file, yet. Otherwise, a upload is triggered on the Browser side.
   * This method returns, if the upload has finished.
   * <br/>
   * Note: This method doesn't fire exceptions. Instead, see {@link #addUploadListener(UploadListener)}
   * and {@link UploadEvent#getUploadException()} on how to get notified about exceptions during upload.
   * @return <code>true</code> if the upload has been started and has finished
   * without an error.
   * @see Upload#addUploadListener(UploadListener)
   */
  public boolean performUpload() {
    checkWidget();

    final boolean uploadSuccessful[] = {false};
    // Always check if user selected a file because otherwise the UploadWidget itself doesn't trigger a POST and therefore, the
    // subsequent loop never terminates.
    if (getPath() != null && !"".equals( getPath() )) {
      if( isEnabled() && !uploadInProgresses[ 0 ] ) {
        UploadListener listener =  new UploadAdapter() {
          public void uploadFinished(UploadEvent event) {
            uploadInProgresses[ 0 ] = false;
            uploadSuccessful[ 0 ] = true;
          }

          public void uploadException( UploadEvent uploadEvent ) {
            uploadInProgresses[ 0 ] = false;
          }
        };
        addUploadListener( listener );
        uploadInProgresses[ 0 ] = true;
        startUpload();
        try {
          while( uploadInProgresses[ 0 ] && !isDisposed()) {
            if( !getDisplay().readAndDispatch() ) {
              getDisplay().sleep();
            }
          }
        } finally {
          uploadInProgresses[ 0 ] = false;
          // 324732: [upload] Widget is disposed if widget is disposed while upload is in progress
          // https://bugs.eclipse.org/bugs/show_bug.cgi?id=324732
          if (!isDisposed()) {
            removeUploadListener( listener );
          }
        }
    }

    }
    
    return uploadSuccessful[ 0 ];
  }
  
  private void startUpload () {
    StringBuffer sb = new StringBuffer();
    sb.append( getWidgetId() );
    sb.append( System.currentTimeMillis() );
    processId = sb.toString();
    UICallBack.activate( processId );
    String url = handler.getUploadUrl();
    browseBtn.submit( url );
  }

  private void initHandler() {
    if (handler == null) {
      FileUploadReceiver receiver = new DiskFileUploadReceiver();
      handler = new FileUploadHandler(receiver);
      listener = new InternalUploadListener();
      handler.addUploadListener( listener );
    }
  }
  

  /**
   * Set the text of the browse button.
   */
  public void setBrowseButtonText( final String browseButtonText ) {
    checkWidget();
    if( browseButtonText == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    browseBtn.setText( browseButtonText );
  }

  /**
   * Returns the text of the browse button.
   */
  public String getBrowseButtonText() {
    checkWidget();
    return browseBtn.getText();
  }

  /**
   * Sets the text of the upload button. Only applies, if {@link #SHOW_UPLOAD_BUTTON}
   * is set as style.
   * @param Text for the upload button, must not be <code>null</code>.
   * @see #Upload(Composite, int, int)
   */
  public void setUploadButtonText( final String uploadButtonText ) {
    checkWidget();
    if( uploadButtonText == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if (uploadBtn != null)
      uploadBtn.setText( uploadButtonText );
  }

  /**
   * Returns the text of the upload button. Can return <code>null</code>.
   */
  public String getUploadButtonText() {
    checkWidget();
    return uploadBtn.getText();
  }

  /**
   * Adds the listener to the collection of listeners who will
   * be notified when the receiver's path is modified, by sending
   * it one of the messages defined in the <code>ModifyListener</code>
   * interface.
   *
   * @param listener the listener which should be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see ModifyListener
   * @see #removeModifyListener
   */
  public void addModifyListener( final ModifyListener listener ) {
    checkWidget();
    ModifyEvent.addListener( this, listener );
  }

  /**
   * Removes the listener from the collection of listeners who will
   * be notified when the receiver's path is modified.
   *
   * @param listener the listener which should no longer be notified
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   *
   * @see ModifyListener
   * @see #addModifyListener
   */
  public void removeModifyListener( final ModifyListener listener ) {
    checkWidget();
    ModifyEvent.removeListener( this, listener );
  }

  /**
   * Gets the name of the last uploaded file. This method
   * can be called even if the upload has not finished yet.
   * @see Upload#getPath()
   *
   * @return The name of the last uploaded file.
   */
  public String getLastFileUploaded() {
    checkWidget();
    return browseBtn.getFileName();
  }

  /**
   * After uploading has finished this method returns the uploaded file
   * and all available meta data, as file name, content type, etc.
   * @throws SWTException SWT.ERROR_WIDGET_DISPOSED if widget is disposed.
   */
  public UploadItem getUploadItem() {
    checkWidget();

    // TODO: [sr] remove if implemented in Widget#checkWidget()
    if (isDisposed()) {
      SWT.error( SWT.ERROR_WIDGET_DISPOSED );
    }

    FileInputStream fileInputStream = null;
    String canonicalPath = null;
    long length = 0;
    String name = null;
    if (uploadedFile != null) {
      try {
        fileInputStream = new FileInputStream( uploadedFile );
      } catch( FileNotFoundException e ) {
        e.printStackTrace();
      }
      try {
        canonicalPath = uploadedFile.getCanonicalPath();
      } catch( IOException e ) {
        e.printStackTrace();
      }
      name = uploadedFile.getName();
      length = uploadedFile.length();
    }
    
    final UploadItem uploadItem = new UploadItem( fileInputStream,
                                                  uploadContentType,
                                                  name,
                                                  canonicalPath,
                                                  length);
    return uploadItem;
  }

  private String getWidgetId() {
    return String.valueOf(this.hashCode());
  }

  /**
   * Adds a new Listener to the Upload.
   *
   * @param listener The new listener.
   */
  public void addUploadListener( final UploadListener listener ) {
    checkWidget();
    UploadEvent.addListener( this, listener );

  }

  /**
   * Removes a Listener from the Upload.
   *
   * @param listener The new listener.
   */
  public void removeUploadListener( final UploadListener listener ) {
    checkWidget();
    UploadEvent.removeListener( this, listener );
  }

  /**
   * {@inheritDoc}
   */
  public void dispose() {
    if (handler != null) {
      handler.dispose();
    }
    super.dispose();
  }

  /**
   * Resets the internal state of the widget so that all information about the last
   * uploaded file are lost. Additionally the text and the progressbar (if visible)
   * are reset to the defaults.
   */
  public void reset() {
    checkWidget();
    if (handler != null) {
      handler.dispose();
      handler = null;
    }
    initHandler();
    uploadInProgresses = new boolean[]{ false };
    fileText.setText( "" );
    if (progressBar != null) {
      progressBar.setSelection( 0 );
      progressBar.setState( SWT.NORMAL );
    }
  }

  /**
   * Returns a configuration facade for this update widget.
   */
  public IUploadConfiguration getConfiguration() {
    return new UploadConfigAdapter(handler);
  }
  
  public void setEnabled( boolean enabled ) {
    super.setEnabled( enabled );
    browseBtn.setEnabled( enabled );
    fileText.setEnabled( enabled );
    
    if( uploadBtn != null ) {
      uploadBtn.setEnabled( enabled );
    }
    if ( progressBar != null ) {
      progressBar.setEnabled( enabled );
    }
  }


  private boolean hasStyle( int testStyle ) {
    return ( flags & ( testStyle ) ) != 0;
  }
  
  private int computeBaseWidth() {
    float avgCharWidth = Graphics.getAvgCharWidth( getFont() );
    return ( int )( avgCharWidth * 25 );
  }
  
  private void createChildren() {
    GridLayout layout = new GridLayout( 3, false );
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    layout.horizontalSpacing = 1;
    layout.verticalSpacing = 1;
    setLayout( layout );
    
    fileText = new Text( this, SWT.BORDER );
    fileText.setToolTipText( "Selected file" );
    fileText.setEditable( false );
    
    browseBtn = new FileUpload( this, SWT.NONE );
    browseBtn.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false ) );
    browseBtn.setText( "Browse" );
    browseBtn.setToolTipText( "Browse to select a single file" );
    browseBtn.addSelectionListener( new SelectionAdapter() {
      
      public void widgetSelected( SelectionEvent event ) {
        String filename = browseBtn.getFileName();
        if( !filename.equals( fileText.getText() ) ) {
          ModifyEvent modifyEvent = new ModifyEvent( Upload.this );
          modifyEvent.processEvent();
          if( progressBar != null && !progressBar.isDisposed() ) {
            progressBar.setSelection( 0 );
            progressBar.setToolTipText( "Upload progress: 0%" );
          }
          fileText.setText( filename );
        }
      }
    } );
    
    GridData textLayoutData = new GridData( SWT.FILL, SWT.FILL, false, false );
    textLayoutData.widthHint = computeBaseWidth();
    if( hasStyle( SHOW_UPLOAD_BUTTON ) ) {
      fileText.setLayoutData( textLayoutData );
      uploadBtn = new Button( this, SWT.PUSH );
      uploadBtn.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false ) );
      uploadBtn.setText( "Upload" );
      uploadBtn.setToolTipText( "Upload selected file" );
      uploadBtn.addSelectionListener( new SelectionAdapter() {
        
        public void widgetSelected( SelectionEvent event ) {
          startUpload();
        }
      } );
    }
    else {
      textLayoutData.horizontalSpan = 2;
      fileText.setLayoutData( textLayoutData );
    }
    
    if( hasStyle( SHOW_PROGRESS ) ) {
      progressBar = new ProgressBar( this, SWT.HORIZONTAL | SWT.SMOOTH );
      GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, false );
      layoutData.horizontalSpan = 3;
      progressBar.setLayoutData( layoutData );
      progressBar.setToolTipText( "Upload progress" );
      progressBar.setMinimum( 0 );
      progressBar.setMaximum( 100 );
    }
  }
  
  private class InternalUploadListener implements IFileUploadListener {
    
    public void uploadFinished( final FileUploadEvent uploadEvent ) {
      browseBtn.getDisplay().asyncExec( new Runnable() {
        public void run() {
          handler.removeUploadListener( listener );
          int percent = 100;
          UploadEvent evt = null;
          try {
            DiskFileUploadReceiver receiver = ( DiskFileUploadReceiver )handler.getReceiver();
            uploadedFile = receiver.getTargetFile();
            evt = new UploadEvent( Upload.this, true, (int)uploadEvent.getBytesRead(), (int)uploadEvent.getContentLength() );
          } catch( Exception e ) {
            evt = new UploadEvent( Upload.this, e );
            percent = 0;
          }
          finally {
            if( progressBar != null && !progressBar.isDisposed() ) {
              progressBar.setSelection( percent );
              progressBar.setToolTipText( "Upload progress: " + percent + "%" );
            }
            evt.processEvent();
          }
          UICallBack.deactivate( processId );
        }
      } );
    }
    
    public void uploadProgress( final FileUploadEvent uploadEvent ) {
      browseBtn.getDisplay().asyncExec( new Runnable() {
        public void run() {
          long bytesRead = uploadEvent.getBytesRead();
          long totalBytes = uploadEvent.getContentLength();
          
          double fraction = bytesRead / ( double )totalBytes;
          int percent = ( int )Math.floor( fraction * 100 );
          if( progressBar != null && !progressBar.isDisposed() ) {
            progressBar.setSelection( percent );
            progressBar.setToolTipText( "Upload progress: " + percent + "%" );
          }
          uploadContentType = uploadEvent.getContentType();
          if (hasStyle( FIRE_PROGRESS_EVENTS )) {
            UploadEvent evt = new UploadEvent( Upload.this, false, (int)bytesRead, (int)totalBytes );
            evt.processEvent();
          }
        }
      } );
    }
    
    public void uploadFailed( final FileUploadEvent uploadEvent ) {
      browseBtn.getDisplay().asyncExec( new Runnable() {
        public void run() {
          if( progressBar != null && !progressBar.isDisposed() ) {
            progressBar.setState( SWT.ERROR );
            progressBar.setSelection( 100 );
            progressBar.setToolTipText( uploadEvent.getException().getMessage() );
          }
          UploadEvent evt = new UploadEvent( Upload.this, uploadEvent.getException() );
          evt.processEvent();
          UICallBack.deactivate( processId );
        }
      } );
    }

  }
  
  private class UploadConfigAdapter implements IUploadConfiguration {
    
    private final FileUploadHandler handler;
    
    public UploadConfigAdapter( FileUploadHandler handler ) {
      this.handler = handler;
    }
    
    public void setFileMaxSize( long maxFileSize ) {
      handler.setMaxFileSize( maxFileSize );
    }
    
    public long getFileSizeMax() {
      return handler.getMaxFileSize();
    }
  }
}