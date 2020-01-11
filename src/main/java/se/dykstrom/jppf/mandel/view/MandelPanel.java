/*
 * Copyright (C) 2019 Johan Dykstrom
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.dykstrom.jppf.mandel.view;

import se.dykstrom.jppf.mandel.model.Line;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static se.dykstrom.jppf.mandel.model.ImageAttributes.INITIAL_IMAGE_SIZE_IN_PIXELS;

/**
 * Displays the Mandelbrot fractal image in a panel. The image is drawn pixel by pixel,
 * using the RGB colors stored in the given {@link Line} objects.
 *
 * @author Johan Dykstrom
 */
class MandelPanel extends JComponent {

    /** The off-screen image buffer. */
    private BufferedImage image;

    public MandelPanel() {
        setPreferredSize(new Dimension(INITIAL_IMAGE_SIZE_IN_PIXELS, INITIAL_IMAGE_SIZE_IN_PIXELS));
    }

    @Override
    public void paintComponent(Graphics graphics) {
        graphics.drawImage(image, 0, 0, null);
    }

    /**
     * Clears the image and panel.
     */
    void clear() {
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        repaint(new Rectangle(0, 0, getWidth(), getHeight()));
    }

    /**
     * Draws one line in the image.
     */
    void draw(Line line) {
        int y = line.getY();
        int[] rgb = line.getRGB();
        for (int x = 0; x < rgb.length; x++) {
            image.setRGB(x, y, rgb[x]);
        }
    }

    /**
     * Finishes by repainting the panel when the image is complete.
     */
    void finish() {
        repaint(new Rectangle(0, 0, getWidth(), getHeight()));
    }
}
