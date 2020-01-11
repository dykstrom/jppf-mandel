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
 * Contains RGB color data for a single line in an image.
 */
public class Line implements Serializable {

    private final int y;
    private final int[] rgb;

    public Line(int y, int[] rgb) {
        this.y = y;
        this.rgb = rgb;
    }

    /**
     * Returns the line number of this line.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns an array of RGB color data for this line.
     */
    public int[] getRGB() {
        return rgb;
    }
}
