/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.SliderTest", {

  extend : qx.core.Object,
  
  construct : function() {
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
  },
    
  members : {

    testCreateSliderByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.Slider );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "slider", widget.getAppearance() );
      assertFalse( widget._horizontal );
      shell.destroy();
      widget.destroy();
    },

    testCreateSliderHorizontalByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.Slider );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "slider", widget.getAppearance() );
      assertTrue( widget._horizontal );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "minimum" : 50
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 50, widget._minimum );
      shell.destroy();
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "maximum" : 150
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 150, widget._maximum );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "selection" : 50
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 50, widget._selection );
      shell.destroy();
      widget.destroy();
    },

    testSetIncrementByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "increment" : 5
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 5, widget._increment );
      shell.destroy();
      widget.destroy();
    },

    testSetPageIncrementByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "pageIncrement" : 20
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 20, widget._pageIncrement );
      shell.destroy();
      widget.destroy();
    },

    testSetThumbByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "thumb" : 20
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 20, widget._thumbLength );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      testUtil.protocolListen( "w3", { "selection" : true } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testCreateDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider();
      assertTrue( slider.isSeeable() );
      slider.destroy();
      testUtil.flush();
      assertTrue( slider.isDisposed() );
    },
    
    testAppearances : function() {
      var slider = this._createSlider();
      assertEquals( "slider", slider.getAppearance() );
      // TODO [tb] : what do we need that subwidget anyway?
      assertEquals( "slider-thumb", slider._thumb.getAppearance() );
      assertEquals( "slider-max-button", slider._maxButton.getAppearance() );
      assertEquals( "slider-min-button", slider._minButton.getAppearance() );
      slider.destroy();
    },

    testStatesHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createSlider( true );
      assertTrue( widget.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget._minButton.hasState( "horizontal" ) );
      assertTrue( widget._maxButton.hasState( "horizontal" ) );
      assertTrue( widget._minButton.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget._maxButton.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget._thumb.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      assertFalse( widget.hasState( "vertical" ) );
      assertFalse( widget._minButton.hasState( "vertical" ) );
      assertFalse( widget._maxButton.hasState( "vertical" ) );
      assertFalse( widget._minButton.hasState( "rwt_VERTICAL" ) );
      assertFalse( widget._maxButton.hasState( "rwt_VERTICAL" ) );
      assertFalse( widget._thumb.hasState( "rwt_VERTICAL" ) );
      widget.destroy();
    },

    testStatesVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createSlider( false );
      assertTrue( widget.hasState( "rwt_VERTICAL" ) );
      assertTrue( widget._minButton.hasState( "vertical" ) );
      assertTrue( widget._maxButton.hasState( "vertical" ) );
      assertTrue( widget._minButton.hasState( "rwt_VERTICAL" ) );
      assertTrue( widget._maxButton.hasState( "rwt_VERTICAL" ) );
      assertTrue( widget._thumb.hasState( "rwt_VERTICAL" ) );
      assertFalse( widget.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget._minButton.hasState( "horizontal" ) );
      assertFalse( widget._maxButton.hasState( "horizontal" ) );
      assertFalse( widget._minButton.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget._maxButton.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget._thumb.hasState( "rwt_HORIZONTAL" ) );
      widget.destroy();
    },
    
    testBasicLayoutVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createSlider( false );
      var slider = testUtil.getElementLayout( widget.getElement() );
      var min = testUtil.getElementLayout( widget._minButton.getElement() );
      var max = testUtil.getElementLayout ( widget._maxButton.getElement() );
      var thumb = testUtil.getElementLayout( widget._thumb.getElement() );
      assertEquals( [ 10, 10, 20, 100 ], slider );
      assertEquals( [ 0, 0, 20, 16 ], min );
      assertEquals( [ 0, 84, 20, 16 ], max );
      assertEquals( [ 0, 16, 20, 7 ], thumb );
      widget.destroy();
    },    
    
    testBasicLayoutHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createSlider( true );
      var slider = testUtil.getElementLayout( widget.getElement() );
      var min = testUtil.getElementLayout( widget._minButton.getElement() );
      var max = testUtil.getElementLayout ( widget._maxButton.getElement() );
      var thumb = testUtil.getElementLayout( widget._thumb.getElement() );
      assertEquals( [ 10, 10, 100, 20 ], slider );
      assertEquals( [ 0, 0, 16, 20 ], min );
      assertEquals( [ 84, 0, 16, 20 ], max );
      assertEquals( [ 16, 0, 7, 20 ], thumb );
      widget.destroy();
    },
    
    testThumbPositionNoScaling : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      // With minimum/maximum initially set to 0/100:
      slider.setHeight( 100 + 2 * 16 ); // to exclude the buttons
      assertEquals( 16 + 0 , thumb.getTop() );
      slider.setSelection( 30 );
      assertEquals( 16 + 30, thumb.getTop() );
      slider.destroy();
    },
    
    testThumbSizeNoScaling : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      slider.setHeight( 100 + 2 * 16 );
      assertEquals( 10 , thumb.getHeight() );
      slider.setThumb( 30 );
      assertEquals( 30, thumb.getHeight() );
      slider.destroy();
    },
    
    testThumbPositionWithScaling : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      slider.setHeight( 100 + 2 * 16 ); 
      slider.setSelection( 25 + 0 );
      slider.setMinimum( 25 );
      slider.setMaximum( 75 );
      assertEquals( 16 + 0, thumb.getTop() );
      slider.setSelection( 35 );
      assertEquals( 16 + 20, thumb.getTop() );
      slider.destroy();
    },
    
    testThumbSizeWithScaling : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      slider.setHeight( 100 + 2 * 16 ); // to exclude the buttons
      assertEquals( 10 , thumb.getHeight() );
      slider.setSelection( 25 + 0 );
      slider.setMinimum( 25 );
      slider.setMaximum( 75 );
      assertEquals( 20 , thumb.getHeight() );
      slider.setThumb( 30 );
      assertEquals( 60, thumb.getHeight() );
      slider.destroy();
    },
    
    testClickOnMaxButton : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      assertEquals( 0, slider._selection );
      testUtil.click( slider._maxButton );
      assertEquals( 5, slider._selection );
      assertTrue( slider._requestScheduled );
      slider.destroy();
    },
    
    testClickOnMinButton : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setSelection( 50 );
      assertEquals( 50, slider._selection );
      testUtil.click( slider._minButton );
      assertEquals( 45, slider._selection );
      assertTrue( slider._requestScheduled );
      slider.destroy();
    },
    
    testClickOnMinButtonLimit : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setSelection( 5 );
      assertEquals( 5, slider._selection );
      testUtil.click( slider._minButton );
      assertEquals( 0, slider._selection );
      assertTrue( slider._requestScheduled );
      slider._requestScheduled = false;
      testUtil.click( slider._minButton );
      assertEquals( 0, slider._selection );
      assertTrue( slider._requestScheduled );
      slider.destroy();
    },

    testClickOnMaxButtonLimit : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setThumb( 7 );
      slider.setSelection( 90 );
      testUtil.click( slider._maxButton );
      assertEquals( 93, slider._selection );
      assertTrue( slider._requestScheduled );
      slider._requestScheduled = false;
      testUtil.click( slider._maxButton );
      assertEquals( 93, slider._selection );
      assertTrue( slider._requestScheduled );
      slider.destroy();
    },
    
    testSendEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setUserData( "id", "w99" );
      testUtil.click( slider._maxButton );
      assertTrue( slider._requestScheduled );
      testUtil.forceTimerOnce();
      assertFalse( slider._requestScheduled );
      assertEquals( 0, testUtil.getRequestsSend() );
      org.eclipse.swt.Request.getInstance().send();
      assertTrue( testUtil.getMessage().indexOf( "w99.selection=5" ) != -1 );
      testUtil.clearRequestLog();
      slider.setHasSelectionListener( true );
      testUtil.click( slider._maxButton );
      assertTrue( slider._requestScheduled );
      testUtil.forceTimerOnce();
      assertFalse( slider._requestScheduled );
      assertEquals( 1, testUtil.getRequestsSend() );
      assertTrue( testUtil.getMessage().indexOf( "widgetSelected=w99" ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( "w99.selection=10" ) != -1 );
      slider.destroy();
    },
    
    testHoldMaxButton : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider._maxButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 5, slider._selection );
      testUtil.forceInterval( slider._delayTimer ); // start scrolling
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 10, slider._selection );
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 15, slider._selection );
      assertTrue( slider._requestScheduled );
      testUtil.forceTimerOnce(); // add request parameter
      assertFalse( slider._requestScheduled );
      testUtil.forceInterval( slider._repeatTimer );
      assertTrue( slider._requestScheduled );
      assertEquals( 20, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseup" );
      assertFalse( slider._repeatTimer.isEnabled() );
    },
    
    testHoldMaxButtonAbort : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider._maxButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 5, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseout" );
      try {
        testUtil.forceInterval( slider._delayTimer ); // try start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );      
      assertEquals( 5, slider._selection );
    },
    
    testHoldMinButton : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb 
      slider.setSelection( 100 );
      assertEquals( 100, slider._selection );
      var node = slider._minButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      testUtil.forceInterval( slider._delayTimer ); // start scrolling
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 90, slider._selection );
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 85, slider._selection );
      assertTrue( slider._requestScheduled );
      testUtil.forceTimerOnce(); // add request parameter
      assertFalse( slider._requestScheduled );
      testUtil.forceInterval( slider._repeatTimer );
      assertTrue( slider._requestScheduled );
      assertEquals( 80, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseup" );
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },
    
    testHoldMinButtonAbort : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb
      slider.setSelection( 100 );
      var node = slider._minButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseout" );
      try {
        testUtil.forceInterval( slider._delayTimer ); // start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );      
      assertEquals( 95, slider._selection );
      slider.destroy();
    },
    
    testHoldMinButtonContinue : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb
      slider.setSelection( 100 );
      var node = slider._minButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseout" );
      testUtil.fakeMouseEventDOM( document.body, "mouseover" );
      testUtil.fakeMouseEventDOM( document.body, "mouseout" );
      testUtil.fakeMouseEventDOM( node, "mouseover" );
      testUtil.forceInterval( slider._delayTimer ); // start scrolling
      assertTrue( slider._repeatTimer.isEnabled() );      
      slider.destroy();
    },
    
    testHoldMinButtonDontContinue : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb
      slider.setSelection( 100 );
      var node = slider._minButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseout" );
      testUtil.fakeMouseEventDOM( document.body, "mouseover" );
      testUtil.fakeMouseEventDOM( document.body, "mouseout" );
      testUtil.fakeMouseEventDOM( node.parentNode, "mouseover" );
      try {
        testUtil.forceInterval( slider._delayTimer ); // start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );      
      slider.destroy();
    },

    testClickLineVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setSelection( 2 );
      assertEquals( 2, slider._selection ); 
      var node = slider.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 16 + 50 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 10 + 16 + 50 );
      assertEquals( 12, slider._selection ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 16 + 1 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 10 + 16 + 1 );
      assertEquals( 2, slider._selection ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 16 + 1 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 10 + 16 + 1 );
      assertEquals( 0, slider._selection );
      slider.setSelection( 85 ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 84 - 1 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 10 + 84 - 1 );
      assertEquals( 90, slider._selection );
      slider.destroy();
    },
    
    testClickLineHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( true );
      slider.setSelection( 2 );
      assertEquals( 2, slider._selection ); 
      var node = slider.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16 + 50, 11 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 10 + 16 + 50, 11 );
      assertEquals( 12, slider._selection ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16 + 1, 11 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 10 + 16 + 1, 11 );
      assertEquals( 2, slider._selection ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16 + 1, 11 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 10 + 16 + 1, 11 );
      assertEquals( 0, slider._selection );
      slider.setSelection( 85 ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 84 - 1, 11 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 10 + 84 - 1, 11 );
      assertEquals( 90, slider._selection );
      slider.destroy();
    },

    testHoldOnLine : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      // scale is 100 - 32 = 68 : 100 => thumb is 6.8
      // last page-increment occured when distance between thumb middle 
      // and mouse is smaller than one increment (also 6.8).
      // Thumb-middle on selection 50 is: 10 + 16 + 50 * 0.68 + 6.8 / 2 = 63.4
      // The mouse will be moved there after the scrolling started:
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 50 );
      assertEquals( 10, slider._selection );
      testUtil.forceInterval( slider._delayTimer ); // start scrolling
      testUtil.forceInterval( slider._repeatTimer );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 11, 63 );
      testUtil.forceInterval( slider._repeatTimer );
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 40, slider._selection );
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 50, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 11, 64 );
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 60, slider._selection );
      // direction change not allowed:
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 11, 30 );
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 60, slider._selection );
      slider.destroy();
    },

    testHoldOnLineAbort : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 50 );
      assertEquals( 10, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 50 );
      try {
        testUtil.forceInterval( slider._delayTimer ); // start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testHoldOnLineMouseUpOnThumb : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      var thumb = slider._thumb.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 50 );
      assertEquals( 10, slider._selection );
      testUtil.fakeMouseEventDOM( thumb, "mouseup", left, 11, 50 );
      try {
        testUtil.forceInterval( slider._delayTimer ); // start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testHoldOnLineMouseOut : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 11, 90 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 90 );
      assertEquals( 10, slider._selection );
      testUtil.forceInterval( slider._delayTimer ); // start scrolling
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 20, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseout", left, 9, 90 );
      testUtil.fakeMouseEventDOM( document.body, "mouseover", left, 9, 90 );
      assertFalse( slider._repeatTimer.isEnabled() );
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 11, 90 );
      assertTrue( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testHoldOnLineMouseOutAbort : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 11, 90 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 90 );
      assertEquals( 10, slider._selection );
      testUtil.forceInterval( slider._delayTimer ); // start scrolling
      testUtil.forceInterval( slider._repeatTimer );
      assertEquals( 20, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseout", left, 9, 90 );
      assertFalse( slider._repeatTimer.isEnabled() );
      testUtil.fakeMouseEventDOM( document.body, "mouseup", left, 0, 0 );
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 11, 90 );
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testKeyControlVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "Right" );
      assertEquals( 5, slider._selection );
      testUtil.press( slider, "Down" );
      assertEquals( 10, slider._selection );
      testUtil.press( slider, "Left" );
      assertEquals( 5, slider._selection );
      testUtil.press( slider, "Up" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "PageDown" );
      assertEquals( 10, slider._selection );
      testUtil.press( slider, "PageUp" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "End" );
      assertEquals( 90, slider._selection );
      testUtil.press( slider, "Right" );
      assertEquals( 90, slider._selection );      
      testUtil.press( slider, "Home" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "Left" );
      assertEquals( 0, slider._selection );      
      slider.destroy();
    },
        
    testKeyControlHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( true );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "Right" );
      assertEquals( 5, slider._selection );
      testUtil.press( slider, "Up" );
      assertEquals( 10, slider._selection );
      testUtil.press( slider, "Left" );
      assertEquals( 5, slider._selection );
      testUtil.press( slider, "Down" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "PageUp" );
      assertEquals( 10, slider._selection );
      testUtil.press( slider, "PageDown" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "End" );
      assertEquals( 90, slider._selection );
      testUtil.press( slider, "Right" );
      assertEquals( 90, slider._selection );      
      testUtil.press( slider, "Home" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "Left" );
      assertEquals( 0, slider._selection );      
      slider.destroy();
    },

    testMouseWheel : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( true );
      assertEquals( 0, slider._selection );
      testUtil.fakeWheel( slider, 1 );
      assertEquals( 0, slider._selection );
      testUtil.fakeWheel( slider, -1 );
      assertEquals( 0, slider._selection );
      testUtil.fakeWheel( slider, -1 );
      assertEquals( 0, slider._selection );
      slider.focus();
      testUtil.fakeWheel( slider, 1 );
      assertEquals( 0, slider._selection );
      testUtil.fakeWheel( slider, -1 );
      assertEquals( 5, slider._selection );
      testUtil.fakeWheel( slider, -1 );
      assertEquals( 10, slider._selection );
      slider.destroy();
    },
  
    testDragThumb : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var left = qx.event.type.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      var node = slider._thumb.getElement();
      assertEquals( 0, slider._selection );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      // Note: 10 pixel = 14.7 units
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 10, 11 );
      assertEquals( 16 + 10, testUtil.getElementBounds( node ).left );
      assertEquals( 15, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 5, 11 );
      assertEquals( 16 + 5, testUtil.getElementBounds( node ).left );
      assertEquals( 7, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 10 + 16 + 5, 11 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 10, 11 );      
      assertEquals( 16 + 5, testUtil.getElementBounds( node ).left );
      assertEquals( 7, slider._selection );
      slider.destroy();
    },
  
    testDragThumbMouseOut : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var left = qx.event.type.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      var node = slider._thumb.getElement();
      assertEquals( 0, slider._selection );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mouseout", left, 10 + 16, 9 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16, 9 );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 5, 9 );
      assertEquals( 16 + 5, testUtil.getElementBounds( node ).left );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 10 + 16 + 5, 9 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 10 );      
      assertEquals( 16 + 5, testUtil.getElementBounds( node ).left );
      slider.destroy();
    },
  
    testDragThumbLimit : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var left = qx.event.type.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      var node = slider._thumb.getElement();
      assertEquals( 0, slider._selection );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 - 10, 11 );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      assertEquals( 0, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 120, 11 );
      assertEquals( 77, testUtil.getElementBounds( node ).left );
      assertEquals( 90, slider._selection );
      slider.destroy();
    },

    testDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( true );
      var thumb = slider._thumb;
      var minButton = slider._minButton;
      var maxButton = slider._maxButton;
      var timer = slider._repeatTimer;
      var parent = slider.getElement().parentNode;
      var parentLength = parent.childNodes.length;
      slider.destroy();
      testUtil.flush();
      assertNull( slider._thumb );
      assertNull( slider._minButton );
      assertNull( slider._maxButton );
      assertNull( slider._repeatTimer );
      assertNull( slider.getElement() );
      assertNull( slider.__listeners );
      assertTrue( parent.childNodes.length === parentLength - 1 );
      assertTrue( minButton.isDisposed() );
      assertTrue( maxButton.isDisposed() );
      assertTrue( timer.isDisposed() );
    },

    /////////
    // Helper
    
    _createSlider : function( horizontal ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var result = new org.eclipse.swt.widgets.Slider( horizontal );
      result.addToDocument();
      result.setLeft( 10 );
      result.setTop( 10 );
      if( horizontal ) {
        result.setWidth( 100 );
        result.setHeight( 20 ); 
      } else {        
        result.setWidth( 20 );
        result.setHeight( 100 ); 
      }
      result.setIncrement( 5 );
      testUtil.flush();
      return result;
    }
    
  }
  
} );