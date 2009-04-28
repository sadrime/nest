/*
 * $Id: RenderedImageIcon.java,v 1.1 2009-04-28 14:17:18 lveci Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.esa.beam.framework.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;

import javax.swing.Icon;

/**
 * An adapter class which supplies a <code>RenderedImage</code> with the <code>Icon</code> interface.
 */
public class RenderedImageIcon implements Icon {

    private RenderedImage _image;

    /**
     * Constructs a new <code>RenderedImageIcon</code> for the given <code>RenderedImage</code>.
     *
     * @param image the image to be wrapped
     */
    public RenderedImageIcon(RenderedImage image) {
        _image = image;
    }

    /**
     * Returns the wrapped <code>RenderedImage</code>.
     */
    public RenderedImage getImage() {
        return _image;
    }

    /**
     * Returns the icon's width.
     *
     * @return an int specifying the fixed width of the icon.
     */
    public int getIconWidth() {
        return _image.getWidth();
    }

    /**
     * Returns the icon's height.
     *
     * @return an int specifying the fixed height of the icon.
     */
    public int getIconHeight() {
        return _image.getHeight();
    }


    /**
     * Draw the icon at the specified location.  Icon implementations may use the Component argument to get properties
     * useful for painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawRenderedImage(_image, AffineTransform.getTranslateInstance(x, y));
    }
}
