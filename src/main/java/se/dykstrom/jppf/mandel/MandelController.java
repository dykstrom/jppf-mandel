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

package se.dykstrom.jppf.mandel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.dykstrom.jppf.mandel.model.Coordinates;
import se.dykstrom.jppf.mandel.rubberband.RubberBandSelectionEvent;
import se.dykstrom.jppf.mandel.task.Parameters;
import se.dykstrom.jppf.mandel.task.TaskSpawner;
import se.dykstrom.jppf.mandel.model.ImageAttributes;
import se.dykstrom.jppf.mandel.model.Line;
import se.dykstrom.jppf.mandel.view.MandelView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static se.dykstrom.jppf.mandel.model.Coordinates.INITIAL_COORDINATES;

public class MandelController {

    private static final int TASKS_PER_JOB = 4;

    private final Logger logger = LoggerFactory.getLogger(MandelController.class);

    private final MandelView view;
    private final int numberOfJobs;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final TaskSpawner spawner = new TaskSpawner();

    /** Stack used to store undo objects, that is, {@code ImageAttributes} objects. */
    private final Stack<ImageAttributes> undoStack = new Stack<>();

    public MandelController(MandelView view, int numberOfJobs) {
        this.view = view;
        this.numberOfJobs = numberOfJobs;
        undoStack.push(ImageAttributes.INITIAL_ATTRIBUTES);

        view.getNewMenuItem().addActionListener(event -> newAction());
        view.getExitMenuItem().addActionListener(event -> exitAction());
        view.getUndoMenuItem().addActionListener(event -> undoAction());
        view.getRubberBandSelector().addRubberBandListener(this::rubberBandAction);
        view.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeAction();
            }
        });
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitAction();
            }
        });

    }

    // --- Actions ---

    private void newAction() {
        Dimension size = view.getImageSize();
        double scale;
        Rectangle bounds;
        if (size.getWidth() > size.getHeight()) {
            scale = Coordinates.INITIAL_SIZE / size.getHeight();
            bounds = new Rectangle(0, 0, (int) size.getHeight(), (int) size.getHeight());
        } else {
            scale = Coordinates.INITIAL_SIZE / size.getWidth();
            bounds = new Rectangle(0, 0, (int) size.getWidth(), (int) size.getWidth());
        }
        Coordinates coordinates = centerImage(size, bounds, scale, INITIAL_COORDINATES.getMinX(), INITIAL_COORDINATES.getMinY());
        undoStack.push(createImage(new ImageAttributes(coordinates, scale)));
    }

    private void exitAction() {
        executorService.shutdown();
        spawner.close();
        System.exit(0);
    }

    private void undoAction() {
        if (undoStack.size() > 1) {
            // Throw away the top item, since that was used to create the current image
            undoStack.pop();

            // Use next item to create a new image, identical to the previous image
            createImage(undoStack.peek());
        }
    }

    private final Debouncer debouncer = new Debouncer(() -> createImage(undoStack.peek()));

    private void resizeAction() {
        debouncer.run();
    }

    private void rubberBandAction(RubberBandSelectionEvent event) {
        Rectangle bounds = event.getSelectionBounds();
        logger.debug("Selected area = {}", bounds);

        ImageAttributes imageAttributes = undoStack.peek();
        Coordinates coordinates = imageAttributes.getCoordinates();
        final double scale = imageAttributes.getScale();

        final double newMinX = coordinates.getMinX() + bounds.getX() * scale;
        final double newMinY = coordinates.getMinY() + bounds.getY() * scale;
        final double newScale = calculateNewScale(view.getImageSize(), bounds, scale);
        final Coordinates newCoordinates = centerImage(view.getImageSize(), bounds, newScale, newMinX, newMinY);

        undoStack.push(createImage(new ImageAttributes(newCoordinates, newScale)));
    }

    /**
     * Calculates new coordinates that center the image after zooming in on a selected area.
     * The intention is to position the selected area in the middle of the new image.
     *
     * @param size The size of the image in pixels.
     * @param bounds The bounds of the selected area in pixels.
     * @param scale The new scale after zooming in.
     * @param minX The new min X after zooming in.
     * @param minY The new min Y after zooming in.
     * @return The coordinates for a centered image.
     */
    private Coordinates centerImage(Dimension size, Rectangle bounds, double scale, double minX, double minY) {
        final double x1 = size.getWidth();
        final double x2 = bounds.getWidth();
        final double y1 = size.getHeight();
        final double y2 = bounds.getHeight();

        if (y2 / y1 > x2 / x1) {
            // Calculate the width of the selected area after zooming in
            double x3 = y1 / y2 * x2;
            double pixelsLeftOfArea = (x1 - x3) / 2;
            return new Coordinates(minX - pixelsLeftOfArea * scale, minY);
        } else {
            // Calculate the height of the selected area after zooming in
            double y3 = x1 / x2 * y2;
            double pixelsAboveArea = (y1 - y3) / 2;
            return new Coordinates(minX, minY - pixelsAboveArea * scale);
        }
    }

    /**
     * Calculates a new scale after zooming in on a selected area. The new scale is calculated from
     * the relation between the image size (size) and the selected area (bounds), and the old scale.
     *
     * @param size The size of the image in pixels.
     * @param bounds The bounds of the selected area in pixels.
     * @param scale The old scale.
     * @return The new scale.
     */
    private double calculateNewScale(Dimension size, Rectangle bounds, double scale) {
        final double x1 = size.getWidth();
        final double x2 = bounds.getWidth();
        final double y1 = size.getHeight();
        final double y2 = bounds.getHeight();
        if (y2 / y1 > x2 / x1) {
            return y2 / y1 * scale;
        } else {
            return x2 / x1 * scale;
        }
    }

    /**
     * Creates a new fractal image that fits the current size of the image panel.
     *
     * @param imageAttributes The image attributes that defines the image to create.
     * @return The actual coordinates use the draw the image.
     */
    private ImageAttributes createImage(final ImageAttributes imageAttributes) {
        Dimension imageSize = view.getImageSize();
        int width = (int) imageSize.getWidth();
        int height = (int) imageSize.getHeight();

        executorService.submit(() -> {
            try {
                List<Line> lines = createLines(width, height, imageAttributes);
                SwingUtilities.invokeLater(() -> view.drawImage(lines));
            } catch (Exception e) {
                logger.error("Error calculating lines: " + e.getMessage(), e);
                SwingUtilities.invokeLater(() -> showMessageDialog(view, "Error calculating lines:\n" + e.getMessage(), "Error", ERROR_MESSAGE));
            }
        });

        return imageAttributes;
    }

    /**
     * Returns a list of lines to draw.
     */
    private List<Line> createLines(int width, int height, ImageAttributes imageAttributes) throws Exception {
        final int numberOfTasks = numberOfJobs * TASKS_PER_JOB;
        final int linesPerTask = height / numberOfTasks;
        logger.info("Number of lines = {}, number of jobs = {}, number of tasks = {}, tasks per job = {}, lines per task = {}",
                height, numberOfJobs, numberOfTasks, TASKS_PER_JOB, linesPerTask);

        // Divide the coordinate space among the tasks
        Coordinates coordinates = imageAttributes.getCoordinates();
        double coordinatesPerTask = height * imageAttributes.getScale() / numberOfTasks;

        List<Parameters> parametersList = new ArrayList<>();

        // Create parameters for all tasks
        for (int s = 0; s < (numberOfTasks - 1); s++) {
            double minY = coordinates.getMinY() + (coordinatesPerTask * s);
            ImageAttributes taskAttributes = imageAttributes.withCoordinates(coordinates.withMinY(minY));
            parametersList.add(new Parameters(linesPerTask * s, width, linesPerTask, taskAttributes));
        }

        // Assign the rest of the lines to the last task
        double minY = coordinates.getMinY() + (coordinatesPerTask * (numberOfTasks - 1));
        ImageAttributes taskAttributes = imageAttributes.withCoordinates(coordinates.withMinY(minY));
        int taskHeight = height - ((numberOfTasks - 1) * linesPerTask);
        parametersList.add(new Parameters(linesPerTask * (numberOfTasks - 1), width, taskHeight, taskAttributes));

        long start = System.nanoTime();
        List<Line> lines = spawner.spawnTasks(parametersList, numberOfJobs);
        long stop = System.nanoTime();
        long durationInMillis = (stop - start) / 1_000_000;
        logger.info("Calculated {} lines in {} ms", height, durationInMillis);
        return lines;
    }
}
