/*******************************************************************************
 * Copyright (c) 2009-2010 David Donahue and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Donahue - initial API, implementation and documentation
 *     Austin Riddle - improvements to widget hierarchy and data flow for 
 *                     consistency with SWT behavior.
 ******************************************************************************/
try {
	google.load("visualization", "1", {packages:["annotatedtimeline"]});
}
catch (e) {
	var mesg = "Error loading Google Annotated Timeline API: "+e;
	if (console) {
		console.log(mesg);
	}
	else {
		alert(mesg);
	}
}

qx.Class.define( "org.eclipse.rap.rwt.visualization.google.AnnotatedTimeLine", {
extend: org.eclipse.rap.rwt.visualization.google.BaseChart,
    
    members : {
      
      _createChart : function(domElement) {
        return new google.visualization.AnnotatedTimeLine(domElement);
      }
        
    }
    
} );

org.eclipse.rap.rwt.visualization.google.BaseChart.registerAdapter(
		"org.eclipse.rap.rwt.visualization.google.AnnotatedTimeLine",
		org.eclipse.rap.rwt.visualization.google.AnnotatedTimeLine);