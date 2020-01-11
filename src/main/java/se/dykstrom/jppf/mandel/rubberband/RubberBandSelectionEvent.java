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

package se.dykstrom.jppf.mandel.rubberband;

import java.awt.Rectangle;
import java.util.EventObject;

/**
 * An event which indicates that a rubber band selection occurred on a component.
 *
 * @author Johan Dykstrom
 */
public class RubberBandSelectionEvent extends EventObject {

    /** The rectangle that bounds the rubber band selection. */
    private final Rectangle bounds;

    /**
     * Creates a new selection event with the given source and selection bounds.
     */
    RubberBandSelectionEvent(Object source, Rectangle bounds) {
        super(source);
        this.bounds = bounds;
    }

    @Override
    public String toString() {
        return RubberBandSelectionEvent.class.getSimpleName() + "[AREA_SELECTED," + bounds + "] on " + source;
    }

    /**
     * Returns the selection bounds.
     */
    public Rectangle getSelectionBounds() {
        return bounds;
    }
}
