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

qx.Class.define( "org.eclipse.rwt.test.tests.GroupTest", {

  extend : qx.core.Object,

  members : {

    testCreateGroupByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Group",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.Group );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "group-box", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Group",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : "foo && <> \" bar"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "foo &amp; &lt;&gt; &quot; bar", widget.getLegend() );
      shell.destroy();
      widget.destroy();
    }
    
    
    // TODO [tb] : breaks IE7 (commented to be able to run all other tests) 
//    testApplyGroupLabelId : function(){
//      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
//      var shell = TestUtil.createShellByProtocol( "w2" );
//      var processor = org.eclipse.rwt.protocol.Processor;
//      processor.processOperation( {
//        "target" : "w3",
//        "action" : "create",
//        "type" : "rwt.widgets.Group",
//        "properties" : {
//          "style" : [],
//          "parent" : "w2"
//        }
//      } );
//      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
//      var widget = ObjectManager.getObject( "w3" );
//      var labelObject = widget.getLegendObject().getLabelObject();
//      
//      assertEquals( "w3-label", labelObject.getHtmlAttribute( "id" ) );
//      
//      shell.destroy();
//      widget.destroy();
//    }

  }
  
} );