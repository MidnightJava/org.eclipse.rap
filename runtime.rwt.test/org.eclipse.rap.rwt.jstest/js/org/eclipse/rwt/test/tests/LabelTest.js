/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var Processor = org.eclipse.rwt.protocol.Processor;
var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;

var shell;
var labelWidget;

qx.Class.define( "org.eclipse.rwt.test.tests.LabelTest", {

  extend : qx.core.Object,
  
  members : {

    testCreateByProtocol : function() {
      this.createLabel( [ "LEFT" ] );

      assertIdentical( shell, labelWidget.getParent() );
      assertTrue( labelWidget.getUserData( "isControl") );
      assertTrue( labelWidget.hasState( "rwt_LEFT" ) );
      assertEquals( "left", labelWidget.getHorizontalChildrenAlign() );
      assertEquals( "top", labelWidget.getVerticalChildrenAlign() );
      assertEquals( "label-wrapper", labelWidget.getAppearance() );
      assertEquals( "hidden", labelWidget.getOverflow() );
      assertEquals( "", this.getTextContent() );
      assertEquals( true, labelWidget.getHideFocus() );
      assertEquals( -1, labelWidget.getFlexibleCell() );
      assertEquals( "label-wrapper", labelWidget.getAppearance() );
      assertFalse( labelWidget._markupEnabled );
    },

    testCreateByProtocolCLabel : function() {
      this.createLabel( [ "LEFT" ], { "appearance" : "clabel" } );

      assertEquals( "clabel", labelWidget.getAppearance() );
    },

    testCreateByProtocolWithMarkupEnabled : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Label",
        "properties" : {
          "style" : [ "LEFT" ],
          "parent" : "w2",
          "markupEnabled" : true
        }
      } );
      labelWidget = ObjectManager.getObject( "w3" );

      assertTrue( labelWidget._markupEnabled );
    },

    testHover : function() {
      this.createLabel( [ "LEFT" ] );

      TestUtil.mouseOver( labelWidget );
      assertTrue( labelWidget.hasState( "over" ) );
      TestUtil.mouseOut( labelWidget );
      assertFalse( labelWidget.hasState( "over" ) );
    },

    testCreateByProtocolWithWRAP : function() {
      this.createLabel( [ "WRAP" ] );

      assertEquals( 1, labelWidget.getFlexibleCell() );
    },

    testSetText : function() {
      this.createLabel( [ "LEFT" ], { "text" : "bla  \n<" } );

      var content = this.getTextContent();
      assertTrue( content === "bla&nbsp; <br>&lt;" || content === "bla&nbsp; <br/>&lt;" );
    },

    testSetTextWithMarkupEnabled : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Label",
        "properties" : {
          "style" : [ "LEFT" ],
          "parent" : "w2",
          "markupEnabled" : true,
          "text" : "<b>foo</b>"
        }
      } );
      TestUtil.flush();
      labelWidget = ObjectManager.getObject( "w3" );

      assertEquals( "<b>foo</b>", this.getTextContent() );
    },

    testSetImageByProtocol : function() {
      this.createLabel( [ "LEFT" ], { "image" : [ "image.png", 10, 20 ] } );

      var src = TestUtil.getCssBackgroundImage( this.getImageElement() );
      assertTrue( src.indexOf( "image.png" ) !== -1 );
    },

    testReSetImage : function() {
      this.createLabel( [ "LEFT" ], { "image" : [ "image.png", 10, 20 ] } );
      
      labelWidget.setImage( null );
      TestUtil.flush();

      assertNull( this.getImageElement() );
      assertEquals( [ 0, 0 ], labelWidget.getCellDimension( 0 ) );
    },

    testSetAlignmentByProtocol : function() {
      this.createLabel( [ "LEFT" ], { "alignment" : "right", "text" : "bla" } ); 

      assertEquals( "right", labelWidget.getHorizontalChildrenAlign() );
      assertEquals( "right", labelWidget.getElement().style.textAlign );
    },
    
    testSetMarginByProtocol : function() {
      this.createLabel( [ "LEFT" ], { 
        "leftMargin" : 1, 
        "topMargin" : 2, 
        "rightMargin" : 3, 
        "bottomMargin" : 4
      } ); 

      assertEquals( 1, labelWidget.getPaddingLeft() );
      assertEquals( 2, labelWidget.getPaddingTop() );
      assertEquals( 3, labelWidget.getPaddingRight() );
      assertEquals( 4, labelWidget.getPaddingBottom() );
    },
    
    /////////
    // helper
    
    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },
    
    tearDown : function() {
      shell.destroy();
      shell = null;
      if( labelWidget !== null ) {
        labelWidget.destroy();
      }
      labelWidget = null;
    },
    
    createLabel : function( styles, properties ) {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Label",
        "properties" : {
          "style" : styles,
          "parent" : "w2"
        }
      } );
      if( properties ) {
        Processor.processOperation( {
          "target" : "w3",
          "action" : "set",
          "properties" : properties
        } );
      }
      labelWidget = ObjectManager.getObject( "w3" );
      TestUtil.flush(); // LabelUtil delays setter for some BS reason...
      TestUtil.flush(); // TWICE!!
    },
    
    getTextElement : function() {
      return labelWidget.getCellNode( 1 );
    },
    
    getTextContent : function() {
      var el = this.getTextElement();
      return el ? el.innerHTML.toLowerCase().replace( /^\s+|\s+$/g, "" ) : "";
    },

    getImageElement : function() {
      return labelWidget.getCellNode( 0 );
    }

  }
  
} );

}());