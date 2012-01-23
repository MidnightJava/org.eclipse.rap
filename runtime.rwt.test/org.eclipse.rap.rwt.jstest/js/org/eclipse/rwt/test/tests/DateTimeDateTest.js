/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
qx.Class.define( "org.eclipse.rwt.test.tests.DateTimeDateTest", {
  extend : qx.core.Object,

  members : {

    monthNames : [ "January",
                   "February",
                   "March",
                   "April",
                   "May",
                   "June",
                   "July",
                   "August",
                   "September",
                   "October",
                   "November",
                   "December",
                   "" ],
    weekdayNames : [ "",
                     "Sunday",
                     "Monday",
                     "Tuesday",
                     "Wednesday",
                     "Thursday",
                     "Friday",
                     "Saturday" ],
    weekdayShortNames : [ "",
                          "Sun",
                          "Mon",
                          "Tue",
                          "Wed",
                          "Thu",
                          "Fri",
                          "Sat" ],
    dateSeparator : "/",
    datePattern : "MDY",

    testCreateDateTimeDateByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      assertTrue( widget instanceof org.eclipse.swt.widgets.DateTimeDate );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl" ) );
      assertEquals( "datetime-date", widget.getAppearance() );
      assertTrue( widget._medium );
      assertFalse( widget._short );
      assertFalse( widget._long );
      assertEquals( 34, org.eclipse.swt.widgets.Calendar.CELL_WIDTH );
      assertEquals( 19, org.eclipse.swt.widgets.Calendar.CELL_HEIGHT );
      assertEquals( this.monthNames, widget._monthname );
      assertEquals( this.weekdayNames, widget._weekday );
      assertEquals( this.weekdayShortNames, org.eclipse.swt.widgets.Calendar.WEEKDAY_NAMES );
      assertEquals( this.dateSeparator, widget._separator1.getText() );
      assertEquals( this.datePattern, widget._datePattern );
      shell.destroy();
      widget.destroy();
    },

    testSetYearByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      TestUtil.protocolSet( "w3", { "year" : 2000 } );
      assertEquals( 2000, widget._yearTextField.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetMonthByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      TestUtil.protocolSet( "w3", { "month" : 6 } );
      assertEquals( "07", widget._monthTextField.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetDayByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      TestUtil.protocolSet( "w3", { "day" : 10 } );
      assertEquals( 10, widget._dayTextField.getText() );
      shell.destroy();
      widget.destroy();
    },

    testSetSubWidgetsBoundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      TestUtil.protocolSet( "w3", { "subWidgetsBounds" : [ [ 0, 3, 5, 0, 18 ], 
                                                           [ 4, 3, 5, 0, 18 ] ] } );
      assertEquals( 3, widget._weekdayTextField.getLeft() );
      assertEquals( 5, widget._weekdayTextField.getTop() );
      assertEquals( 0, widget._weekdayTextField.getWidth() );
      assertEquals( 18, widget._weekdayTextField.getHeight() );
      assertEquals( 3, widget._separator0.getLeft() );
      assertEquals( 5, widget._separator0.getTop() );
      assertEquals( 0, widget._separator0.getWidth() );
      assertEquals( 18, widget._separator0.getHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultDateTimeByProtocol( "w3", "w2", true );
      TestUtil.protocolListen( "w3", { "selection" : true } );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testCreateDateTimeDate : function() {
      var dateTime = this._createDefaultDateTime();
      assertTrue( dateTime instanceof org.eclipse.swt.widgets.DateTimeDate );
      assertEquals( "datetime-date", dateTime.getAppearance() );
      dateTime.destroy();
    },

    testSendAllFieldsTogether : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var dateTime = this._createDefaultDateTime();
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.clearRequestLog();
      dateTime._sendChanges();
      assertEquals( 0, TestUtil.getRequestsSend() );
      var req = org.eclipse.swt.Request.getInstance();
      assertEquals( 10, req._parameters[ "w3.day" ] );
      assertEquals( 10, req._parameters[ "w3.month" ] );
      assertEquals( 2010, req._parameters[ "w3.year" ] );
      dateTime.destroy();
    },

    testSendEvent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var dateTime = this._createDefaultDateTime();
      dateTime.setHasSelectionListener( true );
      dateTime.setDay( 10 );
      dateTime.setMonth( 10 );
      dateTime.setYear( 2010 );
      TestUtil.clearRequestLog();
      dateTime._sendChanges();
      // this should restart the timer, though there is currently no way to test it:
      dateTime._sendChanges(); 
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.forceInterval( dateTime._requestTimer );
      assertFalse( dateTime._requestTimer.getEnabled() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      dateTime.destroy();
    },

    testDropDownCalendar : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dateTime = this._createDefaultDateTime( true );
      TestUtil.click( dateTime._dropDownButton );
      TestUtil.flush();
      var calendar = dateTime._calendar;
      assertTrue( dateTime._calendar.isSeeable() );
      assertEquals( 3, calendar.getLeft() );
      assertEquals( "May fail if browser-window not high enough", 23, calendar.getTop() );
      dateTime.destroy();
    },
    
    testDropDownCalendarNotEnoughSpace : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dateTime = this._createDefaultDateTime( true );
      var browserHeight = qx.html.Window.getInnerHeight( window );
      dateTime.setTop( browserHeight - 40 );
      TestUtil.flush();
      TestUtil.click( dateTime._dropDownButton );
      TestUtil.flush();
      var calendar = dateTime._calendar;
      assertTrue( dateTime._calendar.isSeeable() );
      assertEquals( 3, calendar.getLeft() );
      var expectedTop = dateTime.getTop() - calendar.getHeightValue();
      assertEquals( expectedTop, calendar.getTop() );
      dateTime.destroy();
    },

    // see bug 358531
    testEditingWithKeyboard_InitialEditing : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dateTime = this._createDefaultDateTime();
      dateTime._setDate( new Date( 2011, 8, 2 ) );
      TestUtil.click( dateTime._dayTextField );
      TestUtil.flush();
      TestUtil.pressOnce( dateTime, "1" );
      TestUtil.pressOnce( dateTime, "3" );
      assertEquals( "13", dateTime._dayTextField.getText() );
      dateTime.destroy();
    },

    testEditingWithKeyboard_ContinualEditing : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var dateTime = this._createDefaultDateTime();
      dateTime._setDate( new Date( 2011, 8, 2 ) );
      TestUtil.click( dateTime._dayTextField );
      TestUtil.flush();
      TestUtil.pressOnce( dateTime, "2" );
      TestUtil.pressOnce( dateTime, "1" );
      TestUtil.pressOnce( dateTime, "3" );
      assertEquals( "03", dateTime._dayTextField.getText() );
      dateTime.destroy();
    },

    //////////
    // Helpers
    
    _createDefaultDateTimeByProtocol : function( id, parentId, dropdown ) {
      var styles =  [ "DATE", "MEDIUM" ];
      if( dropdown ) {
        styles[ 2 ] = "DROP_DOWN";
      }
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.DateTime",
        "properties" : {
          "style" : styles,
          "parent" : parentId,
          "cellSize" : [ 34, 19 ],
          "monthNames" : this.monthNames,
          "weekdayNames" : this.weekdayNames,
          "weekdayShortNames" : this.weekdayShortNames,
          "dateSeparator" : this.dateSeparator,
          "datePattern" : this.datePattern
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( "w3" );
    },

    _createDefaultDateTime : function( dropdown ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var style = "medium";
      if( dropdown ) {
        style +=  "|drop_down";
      }
      var dateTime = new org.eclipse.swt.widgets.DateTimeDate( style,
                                                               this.monthNames,
                                                               this.weekdayNames,
                                                               this.weekdayShortNames,
                                                               this.dateSeparator,
                                                               this.datePattern);
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( dateTime, "w3", true );
      dateTime.setSpace( 3, 115, 3, 20 );
      dateTime.addToDocument();
      TestUtil.flush();
      return dateTime;
    }

  }

} );
