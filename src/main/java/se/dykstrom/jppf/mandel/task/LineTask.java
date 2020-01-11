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

import org.jppf.node.protocol.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.dykstrom.jppf.mandel.model.Coordinates;
import se.dykstrom.jppf.mandel.model.Line;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A JPPF task that calculates lines for a specific parameters object. This task may be
 * executed on a remote JPPF node.
 */
public class LineTask extends AbstractTask<List<Line>> {

    private static final long serialVersionUID = 1L;

    private static final int NUM_ITERATIONS = 100;

    /** The RGB colors to use when drawing the image. */
    private static final int[] COLORS = new int[256 * 2];

    /** The factor used to convert the "escape time" value to an RGB color. */
    private static final double FACTOR = (double) (COLORS.length - 1) / NUM_ITERATIONS;

    static {
        int index = 0;
        for (int red = 0; red < 256; red++) {
            COLORS[index++] = (new Color(red, 0, 0)).getRGB();
        }
        for (int green = 0; green < 256; green++) {
            COLORS[index++] = (new Color(255, green, 0)).getRGB();
        }
    }

    private final Logger logger = LoggerFactory.getLogger(LineTask.class);

    private final String name;
    private final Parameters parameters;

    public LineTask(String name, Parameters parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public void run() {
        logger.debug("Running task {}...", name);
        setResult(calcLines());
        logger.debug("Running task {}... done", name);
    }

    private List<Line> calcLines() {
        logger.info("Calculating lines from parameters {}", parameters);
        List<Line> lines = new ArrayList<>();
        for (int y = 0; y < parameters.getHeight(); y++) {
            lines.add(calcLine(y));
        }
        return lines;
    }

    /**
     * Calculates a single line.
     *
     * @param y The line number of the line to calculate.
     * @return The calculated line.
     */
    private Line calcLine(int y) {
        Coordinates coordinates = parameters.getImageAttributes().getCoordinates();
        double scale = parameters.getImageAttributes().getScale();

        final int[] rgb = new int[parameters.getWidth()];
        for (int x = 0; x < rgb.length; x++) {
            int escapeTime = NUM_ITERATIONS - calcPoint(coordinates.getMinX() + x * scale, coordinates.getMinY() + y * scale);
            rgb[x] = COLORS[(int) (escapeTime * FACTOR)];
        }
        return new Line(y + parameters.getFirstY(), rgb);
    }

    /**
     * Returns the "escape time" for the given point, that is, the number of iterations it takes
     * before the point reaches the escape condition. A point that does not reach the escape
     * condition within "the maximum number of iterations" is said to belong to the Mandelbrot set.
     * See also <a href="http://en.wikipedia.org/wiki/Mandelbrot_set">Wikipedia</a>.
     *
     * @param x0 The X start value.
     * @param y0 The Y start value.
     * @return The "escape time" of the given point.
     */
    private int calcPoint(double x0, double y0) {
        double x = x0;
        double y = y0;

        int iteration = 0;

        while ((x * x + y * y <= (2 * 2)) && (iteration < NUM_ITERATIONS)) {
            double tempX = x * x - y * y + x0;
            y = 2 * x * y + y0;
            x = tempX;
            iteration++;
        }

        return iteration;
    }
}
