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

package se.dykstrom.jppf.mandel.task;

import se.dykstrom.jppf.mandel.model.ImageAttributes;

import java.io.Serializable;

/**
 * Contains parameters for calculating one segment of the image, including the y-coordinate for the
 * first pixel line in the segment, the width and height of the segment in pixels, and the image
 * attributes that defines coordinates and scale.
 */
public class Parameters implements Serializable {

    private final int firstY;
    private final int width;
    private final int height;
    private final ImageAttributes imageAttributes;

    public Parameters(int firstY, int width, int height, ImageAttributes imageAttributes) {
        this.firstY = firstY;
        this.width = width;
        this.height = height;
        this.imageAttributes = imageAttributes;
    }

    public int getFirstY() {
        return firstY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImageAttributes getImageAttributes() {
        return imageAttributes;
    }

    @Override
    public String toString() {
        return "[" + firstY + ", " + width + "x" + height + ", " + imageAttributes + "]";
    }
}
