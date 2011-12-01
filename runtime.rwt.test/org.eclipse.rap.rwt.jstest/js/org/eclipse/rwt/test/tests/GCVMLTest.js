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

qx.Class.define( "org.eclipse.rwt.test.tests.GCVMLTest", {
  extend : qx.core.Object,

  members : {
    // NOTE: Testing is only possible in a very limited way

    TARGETENGINE : [ "mshtml" ],
    
    testStrokeProperties : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var VML = org.eclipse.rwt.VML;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      var vmlCanvas = gc._context._canvas;
      assertEquals( 0, vmlCanvas.node.childNodes.length );
      gc._initFields( "10px Arial", [ 1, 2, 3 ], [ 4, 5, 6 ] );
      this._setProperty( gc, "globalAlpha", 0.5 );
      this._setProperty( gc, "lineWidth", 4 );
      this._setProperty( gc, "lineCap", "round" ); // "round"
      this._setProperty( gc, "lineJoin", "bevel" ); // "bevel"
      this._drawRectangle( gc,  10, 10, 20, 20, false );
      assertEquals( 1, vmlCanvas.node.childNodes.length );
      var shape = null;
      for( var hash in vmlCanvas.children ) {
        shape = vmlCanvas.children[ hash ];
      }
      assertIdentical( shape.node, vmlCanvas.node.childNodes[ 0 ] );
      assertEquals( 4, VML.getStrokeWidth( shape ) );
      glob = shape.node.strokeColor;
      assertEquals( [ 4, 5, 6 ], qx.util.ColorUtil.stringToRgb( shape.node.strokeColor.value ) );
      assertNotNull( shape.stroke );
      assertTrue( shape.node.style.filter.indexOf( "opacity=50" ) != -1 );
      assertEquals( "round", shape.stroke.endcap ); 
      assertEquals( "bevel", shape.stroke.joinstyle ); 
      canvas.destroy();
      testUtil.flush();      
    },
    
    testFillProperties : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var VML = org.eclipse.rwt.VML;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      var vmlCanvas = gc._context._canvas;
      assertEquals( 0, vmlCanvas.node.childNodes.length );
      gc._initFields( "10px Arial", [ 1, 2, 3 ], [ 4, 5, 6 ] );
      this._setProperty( gc, "globalAlpha", 0.5 );
      this._drawRectangle( gc, 10, 10, 20, 20, true );
      assertEquals( 1, vmlCanvas.node.childNodes.length );
      var shape = null;
      for( var hash in vmlCanvas.children ) {
        shape = vmlCanvas.children[ hash ];
      }
      assertIdentical( shape.node, vmlCanvas.node.childNodes[ 0 ] );
      assertEquals( [ 1, 2, 3 ], qx.util.ColorUtil.stringToRgb( VML.getFillColor( shape ) ) );
      assertTrue( shape.node.style.filter.indexOf( "opacity=50" ) != -1 );
      canvas.destroy();
      testUtil.flush();      
    },
    
    testInit : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      var vmlCanvas = gc._context._canvas;
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      gc.draw( [ [ "beginPath" ], [ "moveTo", 10.5, 10.5 ], [ "lineTo", 20.5, 10.5 ], [ "stroke" ] ] );
      assertEquals( 1, vmlCanvas.node.childNodes.length );
      gc.init( 400, 500, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      assertEquals( 0, vmlCanvas.node.childNodes.length );
      canvas.destroy();
      testUtil.flush();
    },

    testDrawLine : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      gc.draw( [ [ "beginPath" ], [ "moveTo", 10.5, 10.5 ], [ "lineTo", 20.5, 10.5 ], [ "stroke" ] ] );
      assertEquals( "m100,100 l200,100 e", this._getLastPath( gc ) );
      canvas.destroy();
      testUtil.flush();
    },

    testDrawPoint : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      context = gc._context;
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      gc.draw( [ [ "beginPath" ], [ "rect", 40, 30, 1, 1 ], [ "fill" ] ] ); 
      var expected = "m395,295 l405,295,405,305,395,305 xe";
      var path = this._getLastPath( gc );
      
      assertEquals( expected, path );
      assertTrue( gc._canvas.lastChild.fill.on );
      canvas.destroy();
      testUtil.flush();
    },
    
    testDrawRectangle : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      context = gc._context;
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      gc.draw( [ [ "beginPath" ], [ "rect", 10.5, 20.5, 30, 40 ], [ "stroke" ] ] );
      var expected = "m100,200 l400,200,400,600,100,600 xe";
      assertEquals( expected, this._getLastPath( gc ) );
      canvas.destroy();
      testUtil.flush();
    },

    testDrawRoundRectangle : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      context = gc._context;
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      this._drawRoundRectangle( gc, 2, 4, 20, 30, 4, 10 );      
      var expected =   "m15,95 l15,275 qb15,335 l45,335,185,335 qb215,335 l215,275,215,95 qb215,35 l185,35,45,35 qb15,35 l15,95 e";
      assertEquals( expected, this._getLastPath( gc ) );
      canvas.destroy();
      testUtil.flush();
    },

    testFillGradientRectangle : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      context = gc._context;
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      this._fillGradientRectangle( gc, 40, 60, -30, -40, [ 255, 0, 0 ], [ 0, 0, 255 ] );      
      var expected = "m395,595 l95,595,95,195,395,195 xe"
      assertEquals( expected, this._getLastPath( gc ) );
      expected = "0 red;.25 #bf0040;.5 purple;.75 #4000bf;1 blue";
      assertEquals( expected, gc._canvas.lastChild.fill.colors.value );
      assertEquals( 270, gc._canvas.lastChild.fill.angle );
      assertEquals( "blue", gc._canvas.lastChild.fill.color2.value );
      canvas.destroy();
      testUtil.flush();
    },
    
    testDrawArc : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      //gc.drawArc( 100, 100, 60, 30, 180, 180, true );
      var x = 100;
      var y = 100;
      var width = 60;
      var height = 30;
      var startAngle = 180 * Math.PI / 180;
      var arcAngle = 180 * Math.PI / 180;
      var radiusX = width / 2;
      var radiusY = height / 2;
      gc.draw( [
        [ "beginPath" ],
        [ "arc", x + radiusX, y + radiusY, radiusX, radiusY, -1 * startAngle, -1 * ( startAngle + arcAngle ), true ],
        [ "fill" ]
      ] );
      var expected = "m995,1145 ae1295,1145,300,150,11796300,11796300 e";
      assertEquals( expected, this._getLastPath( gc ) );      
      canvas.destroy();
      testUtil.flush();
    },

    testDrawPolyline : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      gc.draw( [
        [ "beginPath" ],
        [ "moveTo", 10, 10 ],
        [ "lineTo", 100, 70, ],
        [ "lineTo", 70, 100, ],
        [ "lineTo", 10, 10, ],
        [ "fill" ]
      ] );      
      var expected = "m95,95 l995,695,695,995,95,95 e";
      assertEquals( expected, this._getLastPath( gc ) );
      canvas.destroy();
      testUtil.flush();
    },
    
    testDrawImage : function() {
      // NOTE: drawImage can not be tested directly, test "setImageData" instead
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      gc.init( 300, 300, "10px Arial", [ 255, 0, 0 ], [ 0, 0, 255 ] );
      var vmlCanvas = gc._context._canvas;
      var shape = org.eclipse.rwt.VML.createShape( "image" );
      org.eclipse.rwt.VML.setImageData( shape, 
                                        "test.jpg", 
                                        40, 
                                        50, 
                                        100, 
                                        200, 
                                        [ 0.1, 0.2, 0.3, 0.4 ] );
      org.eclipse.rwt.VML.addToCanvas( vmlCanvas, shape );
      assertEquals( "test.jpg", shape.node.src );
      assertEquals( 40, parseInt( shape.node.style.left ) );
      assertEquals( 50, parseInt( shape.node.style.top ) );
      assertEquals( 100, parseInt( shape.node.style.width ) );
      assertEquals( 200, parseInt( shape.node.style.height ) );
      assertEquals( 1, Math.round( shape.node.cropTop * 10 ) );
      assertEquals( 2, Math.round( shape.node.cropRight * 10 ) );
      assertEquals( 3, Math.round( shape.node.cropBottom * 10 ) );
      assertEquals( 4, Math.round( shape.node.cropLeft * 10 ) );
      canvas.destroy();
      testUtil.flush();
    },

    testRestoreColorsOnDomInsert : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      var vmlCanvas = gc._context._canvas;
      gc._initFields( "10px Arial", [ 1, 2, 3 ], [ 4, 5, 6 ] );
      this._drawRectangle( gc,  10, 10, 20, 20, true )
      this._drawRectangle( gc,  10, 10, 20, 20, false );
      var node1 = vmlCanvas.node.childNodes[ 0 ];
      var node2 = vmlCanvas.node.childNodes[ 1 ];
      assertEquals( [ 1, 2, 3 ], qx.util.ColorUtil.stringToRgb( node1.fill.color.value ) );
      assertEquals( [ 4, 5, 6 ], qx.util.ColorUtil.stringToRgb( node2.stroke.color.value ) );
      canvas.setBackgroundGradient( [ [ 0, "red" ], [ 1, "green" ] ] );
      assertEquals( [ 1, 2, 3 ], qx.util.ColorUtil.stringToRgb( node1.fill.color.value ) );
      assertEquals( [ 4, 5, 6 ], qx.util.ColorUtil.stringToRgb( node2.stroke.color.value ) );
      canvas.destroy();
      testUtil.flush();
    },

    testRestoreStrokeWeightOnDomInsert : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var canvas = new org.eclipse.swt.widgets.Composite();
      canvas.setDimension( 300, 300 );
      canvas.addToDocument();
      testUtil.flush();
      var gc = new org.eclipse.swt.graphics.GC( canvas );
      var vmlCanvas = gc._context._canvas;
      gc._initFields( "10px Arial", [ 1, 2, 3 ], [ 4, 5, 6 ] );
      this._setProperty( gc, "lineWidth", 4 ); 
      this._drawRectangle( gc, 10, 10, 20, 20, false );
      var node = vmlCanvas.node.childNodes[ 0 ];
      assertEquals( 3, node.stroke.weight ); // returns value in "pt"
      canvas.setBackgroundGradient( [ [ 0, "red" ], [ 1, "green" ] ] );
      assertEquals( 3, node.stroke.weight );
      canvas.destroy();
      testUtil.flush();
    },
    
    /////////
    // Helper

    _getLastPath : function( gc ) {
      try {
        var result = gc._context._canvas.node.lastChild.path.v.toLowerCase();
      } catch( ex ) {
        // NOTE: Due to a ie-bug, filles paths cant be read from DOM.
        throw new Error( "_getLastPath failed" );
      }
      if( result.charAt( 0 ) == " " ) {
        result = result.slice( 1 );
      }
      return result;
    },

    _setProperty : function( gc, property, value ) {
      gc.draw( [ [ property, value ] ] );
    },
    
    _drawRectangle : function( gc, x, y, width, height, fill ) {
      gc.draw( [ [ "beginPath" ], [ "rect", x, y, width, height ], [ fill ? "fill" : "stroke" ] ] );
    },

    _drawRoundRectangle : function( gc, targetX, targetY, width, height, arcWidth, arcHeight ) {
      var x = targetX;
      var y = targetY;
      // NOTE: the added "+1" in arcSize is the result of a visual comparison of RAP to SWT/Win.  
      var arcWidthHalf = arcWidth / 2 + 1;
      var arcHeightHalf = arcHeight / 2 + 1;
      gc.draw( [
        [ "beginPath" ],
        [ "moveTo", x, y + arcHeightHalf ],
        [ "lineTo", x, y + height - arcHeightHalf ],
        [ "quadraticCurveTo", x, y + height, x + arcWidthHalf, y + height ],
        [ "lineTo", x + width - arcWidthHalf, y + height ],
        [ "quadraticCurveTo", x + width, y + height, x + width, y + height - arcHeightHalf ],
        [ "lineTo", x + width, y + arcHeightHalf ],
        [ "quadraticCurveTo", x + width, y, x + width - arcWidthHalf, y ],
        [ "lineTo", x + arcWidthHalf, y ],
        [ "quadraticCurveTo", x, y, x, y + arcHeightHalf ],
        [ "fill" ]
      ] );
    },
    
    _fillGradientRectangle : function( gc, x, y, width, height, startColor, endColor ) {
      var x1 = x;
      var y1 = y;
      var x2 = x1 + Math.abs( width );
      var y2 = y1;
      gc.draw( [
        [ "createLinearGradient", x1, y1, x2, y2 ],
        [ "addColorStop", 0, startColor ],
        [ "addColorStop", 1, endColor ],
        [ "fillStyle", "linearGradient" ],
        [ "beginPath" ], 
        [ "rect", x, y, width, height ], 
        [ "fill" ]
      ] );
    }
  
  }

} );