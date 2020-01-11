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

/**
 * Represents the coordinates in the Mandelbrot coordinate space used to calculate the image,
 * or one part of the image.
 *
 * @author Johan Dykstrom
 */
public class Coordinates implements Serializable {

    public static final Coordinates INITIAL_COORDINATES = new Coordinates(-2.0, -1.5);

    public static final double INITIAL_SIZE = 3.0;

    private final double minX;
    private final double minY;

    public Coordinates(double minX, double minY) {
        this.minX = minX;
        this.minY = minY;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public Coordinates withMinY(double minY) {
        return new Coordinates(minX, minY);
    }

    @Override
    public String toString() {
        return "[" + minX + ", " + minY + "]";
    }
}
