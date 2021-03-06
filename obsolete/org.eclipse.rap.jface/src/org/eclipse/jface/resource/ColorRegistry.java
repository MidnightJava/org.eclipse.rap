/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.resource;

import java.util.*;

import org.eclipse.core.runtime.Assert;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

/**
 * A color registry maintains a mapping between symbolic color names and SWT
 * <code>Color</code>s.
 * <p>
 * A color registry owns all of the <code>Color</code> objects registered with
 * it, and automatically disposes of them when the SWT Display that creates the
 * <code>Color</code>s is disposed. Because of this, clients do not need to
 * (indeed, must not attempt to) dispose of <code>Color</code> objects
 * themselves.
 * </p>
 * <p>
 * Methods are provided for registering listeners that will be kept
 * apprised of changes to list of registed colors.
 * </p>
 * <p>
 * Clients may instantiate this class (it was not designed to be subclassed).
 * </p>
 *
 * @since 1.0
 */
public class ColorRegistry extends ResourceRegistry {

    /**
     * This registries <code>Display</code>. All colors will be allocated using
     * it.
     */
    protected Device display;

    /**
     * Collection of <code>Color</code> that are now stale to be disposed when
     * it is safe to do so (i.e. on shutdown).
     */
    private List staleColors = new ArrayList();

    /**
     * Table of known colors, keyed by symbolic color name (key type: <code>String</code>,
     * value type: <code>org.eclipse.swt.graphics.Color</code>.
     */
    private Map stringToColor = new HashMap(7);

    /**
     * Table of known color data, keyed by symbolic color name (key type:
     * <code>String</code>, value type: <code>org.eclipse.swt.graphics.RGB</code>).
     */
    private Map stringToRGB = new HashMap(7);

    /**
     * Runnable that cleans up the manager on disposal of the display.
     */
    protected Runnable displayRunnable = new Runnable() {
        public void run() {
            clearCaches();
        }
    };

    /**
     * Create a new instance of the receiver that is hooked to the current
     * display.
     *
     * @see org.eclipse.swt.widgets.Display#getCurrent()
     */
    public ColorRegistry() {
        this(Display.getCurrent(), true);
    }

    /**
     * Create a new instance of the receiver.
     *
     * @param display the <code>Display</code> to hook into.
     */
    public ColorRegistry(Display display) {
    	this (display, true);
    }

    /**
     * Create a new instance of the receiver.
     *
     * @param display the <code>Display</code> to hook into
     * @param cleanOnDisplayDisposal
	 *            whether all fonts allocated by this <code>ColorRegistry</code>
	 *            should be disposed when the display is disposed
     * @since 1.0
     */
    public ColorRegistry(Display display, boolean cleanOnDisplayDisposal) {
        Assert.isNotNull(display);
        this.display = display;
        if (cleanOnDisplayDisposal) {
			hookDisplayDispose();
		}
    }

    /**
     * Create a new <code>Color</code> on the receivers <code>Display</code>.
     *
     * @param rgb the <code>RGB</code> data for the color.
     * @return the new <code>Color</code> object.
     *
     * @since 1.0
     */
    private Color createColor(RGB rgb) {
      return Graphics.getColor( rgb );
//        return new Color(display, rgb);
    }

    /**
     * Dispose of all of the <code>Color</code>s in this iterator.
     *
     * @param iterator over <code>Collection</code> of <code>Color</code>
     */
    private void disposeColors(Iterator iterator) {
//        while (iterator.hasNext()) {
//            Object next = iterator.next();
//            ((Color) next).dispose();
//        }
    }

    /**
     * Returns the <code>color</code> associated with the given symbolic color
     * name, or <code>null</code> if no such definition exists.
     *
     * @param symbolicName symbolic color name
     * @return the <code>Color</code> or <code>null</code>
     */
    public Color get(String symbolicName) {

        Assert.isNotNull(symbolicName);
        Object result = stringToColor.get(symbolicName);
        if (result != null) {
			return (Color) result;
		}

        Color color = null;

        result = stringToRGB.get(symbolicName);
        if (result == null) {
			return null;
		}

        color = createColor((RGB) result);

        stringToColor.put(symbolicName, color);

        return color;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.resource.ResourceRegistry#getKeySet()
     */
    public Set getKeySet() {
        return Collections.unmodifiableSet(stringToRGB.keySet());
    }

    /**
     * Returns the color data associated with the given symbolic color name.
     *
     * @param symbolicName symbolic color name.
     * @return the <code>RGB</code> data.
     */
    public RGB getRGB(String symbolicName) {
        Assert.isNotNull(symbolicName);
        return (RGB) stringToRGB.get(symbolicName);
    }

    /**
     * Returns the color descriptor associated with the given symbolic color name.
     * @since 1.0
     *
     * @param symbolicName
     * @return the color descriptor associated with the given symbolic color name.
     */
    public ColorDescriptor getColorDescriptor(String symbolicName) {
        return ColorDescriptor.createFrom(getRGB(symbolicName));
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.resource.ResourceRegistry#clearCaches()
     */
    protected void clearCaches() {
        disposeColors(stringToColor.values().iterator());
        disposeColors(staleColors.iterator());
        stringToColor.clear();
        staleColors.clear();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.resource.ResourceRegistry#hasValueFor(java.lang.String)
     */
    public boolean hasValueFor(String colorKey) {
        return stringToRGB.containsKey(colorKey);
    }

    /**
     * Hook a dispose listener on the SWT display.
     */
    private void hookDisplayDispose() {
//        display.disposeExec(displayRunnable);
    	displayRunnable.run();
    }

    /**
     * Adds (or replaces) a color to this color registry under the given
     * symbolic name.
     * <p>
     * A property change event is reported whenever the mapping from a symbolic
     * name to a color changes. The source of the event is this registry; the
     * property name is the symbolic color name.
     * </p>
     *
     * @param symbolicName the symbolic color name
     * @param colorData an <code>RGB</code> object
     */
    public void put(String symbolicName, RGB colorData) {
        put(symbolicName, colorData, true);
    }

    /**
     * Adds (or replaces) a color to this color registry under the given
     * symbolic name.
     * <p>
     * A property change event is reported whenever the mapping from a symbolic
     * name to a color changes. The source of the event is this registry; the
     * property name is the symbolic color name.
     * </p>
     *
     * @param symbolicName the symbolic color name
     * @param colorData an <code>RGB</code> object
     * @param update - fire a color mapping changed if true. False if this
     *            method is called from the get method as no setting has
     *            changed.
     */
    private void put(String symbolicName, RGB colorData, boolean update) {

        Assert.isNotNull(symbolicName);
        Assert.isNotNull(colorData);

        RGB existing = (RGB) stringToRGB.get(symbolicName);
        if (colorData.equals(existing)) {
			return;
		}

        Color oldColor = (Color) stringToColor.remove(symbolicName);
        stringToRGB.put(symbolicName, colorData);
        if (update) {
			fireMappingChanged(symbolicName, existing, colorData);
		}

        if (oldColor != null) {
			staleColors.add(oldColor);
		}
    }
}
