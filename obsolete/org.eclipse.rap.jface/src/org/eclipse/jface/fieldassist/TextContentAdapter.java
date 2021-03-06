/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.fieldassist;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * An {@link IControlContentAdapter} for SWT Text controls. This is a
 * convenience class for easily creating a {@link ContentProposalAdapter} for
 * text fields.
 * 
 * @since 1.0
 */
public class TextContentAdapter implements IControlContentAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.taskassistance.IControlContentAdapter#getControlContents(org.eclipse.swt.widgets.Control)
	 */
	public String getControlContents(Control control) {
		return ((Text) control).getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.fieldassist.IControlContentAdapter#setControlContents(org.eclipse.swt.widgets.Control,
	 *      java.lang.String, int)
	 */
	public void setControlContents(Control control, String text,
			int cursorPosition) {
		((Text) control).setText(text);
		((Text) control).setSelection(cursorPosition, cursorPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.fieldassist.IControlContentAdapter#insertControlContents(org.eclipse.swt.widgets.Control,
	 *      java.lang.String, int)
	 */
	public void insertControlContents(Control control, String text,
			int cursorPosition) {
		Point selection = ((Text) control).getSelection();
		((Text) control).insert(text);
		// Insert will leave the cursor at the end of the inserted text. If this
		// is not what we wanted, reset the selection.
		if (cursorPosition < text.length()) {
			((Text) control).setSelection(selection.x + cursorPosition,
					selection.x + cursorPosition);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.fieldassist.IControlContentAdapter#getCursorPosition(org.eclipse.swt.widgets.Control)
	 */
	public int getCursorPosition(Control control) {
//		return ((Text) control).getCaretPosition();
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.fieldassist.IControlContentAdapter#getInsertionBounds(org.eclipse.swt.widgets.Control)
	 */
	public Rectangle getInsertionBounds(Control control) {
		Text text = (Text) control;
//		Point caretOrigin = text.getCaretLocation();
		Point caretOrigin = new Point( 0, 0 );
//		return new Rectangle(caretOrigin.x, caretOrigin.y, 1, text
//				.getLineHeight());
		return new Rectangle(caretOrigin.x, 
		                     caretOrigin.y,
		                     1, 
		                     text.getBounds().height );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.fieldassist.IControlContentAdapter#setCursorPosition(org.eclipse.swt.widgets.Control,
	 *      int)
	 */
	public void setCursorPosition(Control control, int position) {
		((Text) control).setSelection(new Point(position, position));
	}
}
