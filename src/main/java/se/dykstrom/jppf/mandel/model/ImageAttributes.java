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

package se.dykstrom.jppf.mandel.model;

import java.io.Serializable;

import static se.dykstrom.jppf.mandel.model.Coordinates.INITIAL_COORDINATES;
import static se.dykstrom.jppf.mandel.model.Coordinates.INITIAL_SIZE;

/**
 * Defines attributes needed to draw an image, that is coordinates for the upper left corner in the Mandelbrot
 * coordinate space, and a scale to convert between Mandelbrot coordinates and pixels.
 */
public class ImageAttributes implements Serializable {

    /** The initial width and height of the image in pixels. */
    public static final int INITIAL_IMAGE_SIZE_IN_PIXELS = 500;

    public static final ImageAttributes INITIAL_ATTRIBUTES = new ImageAttributes(INITIAL_COORDINATES, INITIAL_SIZE / INITIAL_IMAGE_SIZE_IN_PIXELS);

    private final Coordinates coordinates;
    private final double scale;

    public ImageAttributes(Coordinates coordinates, double scale) {
        this.coordinates = coordinates;
        this.scale = scale;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Returns the scale, that is, the amount of Mandelbrot coordinate space per pixel.
     */
    public double getScale() {
        return scale;
    }

    public ImageAttributes withCoordinates(Coordinates coordinates) {
        return new ImageAttributes(coordinates, scale);
    }

    @Override
    public String toString() {
        return "[" + coordinates + ", " + scale + "]";
    }
}
