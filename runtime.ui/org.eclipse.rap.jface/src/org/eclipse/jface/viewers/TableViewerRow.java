/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 *                            - fix in bug: 174355,195908,198035,215069,227421
 *******************************************************************************/

package org.eclipse.jface.viewers;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

/**
 * TableViewerRow is the Table specific implementation of ViewerRow
 * @since 1.0
 *
 */
public class TableViewerRow extends ViewerRow {
  private TableItem item;

  /**
   * Create a new instance of the receiver from item.
   * @param item
   */
  TableViewerRow(TableItem item) {
    this.item = item;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getBounds(int)
   */
  public Rectangle getBounds(int columnIndex) {
    return item.getBounds(columnIndex);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getBounds()
   */
  public Rectangle getBounds() {
    return item.getBounds();
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getItem()
   */
  public Widget getItem() {
    return item;
  }

  void setItem(TableItem item) {
    this.item = item;
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getColumnCount()
   */
  public int getColumnCount() {
    return item.getParent().getColumnCount();
  }

  // RAP [if] Override ViewerRow method to respect the column order (needed for fixed columns)
  public int getColumnIndex( Point point ) {
    int result = -1;
    int count = getColumnCount();
    // If there are no columns the column-index is 0
    if( count == 0 ) {
      result = 0;
    } else {
      int[] columnOrder = item.getParent().getColumnOrder();
      for( int i = 0; i < columnOrder.length && result == -1; i++ ) {
        if( getBounds( columnOrder[ i ] ).contains( point ) ) {
          result = columnOrder[ i ];
        }
      }
    }
    return result;
  }
  // RAPEND

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getBackground(int)
   */
  public Color getBackground(int columnIndex) {
    return item.getBackground(columnIndex);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getFont(int)
   */
  public Font getFont(int columnIndex) {
    return item.getFont(columnIndex);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getForeground(int)
   */
  public Color getForeground(int columnIndex) {
    return item.getForeground(columnIndex);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getImage(int)
   */
  public Image getImage(int columnIndex) {
    return item.getImage(columnIndex);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getText(int)
   */
  public String getText(int columnIndex) {
    return item.getText(columnIndex);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#setBackground(int, org.eclipse.swt.graphics.Color)
   */
  public void setBackground(int columnIndex, Color color) {
    item.setBackground(columnIndex, color);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#setFont(int, org.eclipse.swt.graphics.Font)
   */
  public void setFont(int columnIndex, Font font) {
    item.setFont(columnIndex, font);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#setForeground(int, org.eclipse.swt.graphics.Color)
   */
  public void setForeground(int columnIndex, Color color) {
    item.setForeground(columnIndex, color);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#setImage(int, org.eclipse.swt.graphics.Image)
   */
  public void setImage(int columnIndex, Image image) {
    Image oldImage = item.getImage(columnIndex);
    if (oldImage != image) {
      item.setImage(columnIndex,image);
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#setText(int, java.lang.String)
   */
  public void setText(int columnIndex, String text) {
    item.setText(columnIndex, text == null ? "" : text); //$NON-NLS-1$
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getControl()
   */
  public Control getControl() {
    return item.getParent();
  }

  public ViewerRow getNeighbor(int direction, boolean sameLevel) {
    if( direction == ViewerRow.ABOVE ) {
      return getRowAbove();
    } else if( direction == ViewerRow.BELOW ) {
      return getRowBelow();
    } else {
      throw new IllegalArgumentException("Illegal value of direction argument."); //$NON-NLS-1$
    }
  }


  private ViewerRow getRowAbove() {
    int index = item.getParent().indexOf(item) - 1;

    if( index >= 0 ) {
      return new TableViewerRow(item.getParent().getItem(index));
    }

    return null;
  }

  private ViewerRow getRowBelow() {
    int index = item.getParent().indexOf(item) + 1;

    if( index < item.getParent().getItemCount() ) {
      TableItem tmp = item.getParent().getItem(index);
      //TODO NULL can happen in case of VIRTUAL => How do we deal with that
      if( tmp != null ) {
        return new TableViewerRow(tmp);
      }
    }

    return null;
  }

  public TreePath getTreePath() {
    return new TreePath(new Object[] {item.getData()});
  }

  public Object clone() {
    return new TableViewerRow(item);
  }

  public Object getElement() {
    return item.getData();
  }

  public int getVisualIndex(int creationIndex) {
    int[] order = item.getParent().getColumnOrder();

    for (int i = 0; i < order.length; i++) {
      if (order[i] == creationIndex) {
        return i;
      }
    }

    return super.getVisualIndex(creationIndex);
  }

  public int getCreationIndex(int visualIndex) {
    if( item != null && ! item.isDisposed() && hasColumns() && isValidOrderIndex(visualIndex) ) {
      return item.getParent().getColumnOrder()[visualIndex];
    }
    return super.getCreationIndex(visualIndex);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getTextBounds(int)
   */
  public Rectangle getTextBounds(int index) {
    return item.getTextBounds(index);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.ViewerRow#getImageBounds(int)
   */
  public Rectangle getImageBounds(int index) {
    return item.getImageBounds(index);
  }

  private boolean hasColumns() {
    return this.item.getParent().getColumnCount() != 0;
  }

  private boolean isValidOrderIndex(int currentIndex) {
    return currentIndex < this.item.getParent().getColumnOrder().length;
  }
  
  int getWidth(int columnIndex) {
    return item.getParent().getColumn(columnIndex).getWidth();
  }
  
  protected boolean scrollCellIntoView(int columnIndex) {
    item.getParent().showItem(item);
    if( hasColumns() ) {
      item.getParent().showColumn(item.getParent().getColumn(columnIndex));  
    }
    
    return true;
  }
}
