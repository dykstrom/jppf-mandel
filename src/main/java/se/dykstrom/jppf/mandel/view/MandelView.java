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
import se.dykstrom.jppf.mandel.rubberband.RubberBandSelector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Creates the main view and all its components.
 *
 * @author Johan Dykstrom
 */
public class MandelView extends JFrame {

    private MandelPanel mandelPanel;

    private JMenuItem exitMenuItem;
    private JMenuItem newMenuItem;
    private JMenuItem undoMenuItem;

    private RubberBandSelector rubberBandSelector;

    public MandelView() {
        initComponents();
    }

    public JMenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    public JMenuItem getNewMenuItem() {
        return newMenuItem;
    }

    public JMenuItem getUndoMenuItem() {
        return undoMenuItem;
    }

    public RubberBandSelector getRubberBandSelector() {
        return rubberBandSelector;
    }

    /**
     * Returns the size of the image as a Dimension object.
     */
    public Dimension getImageSize() {
        return mandelPanel.getSize();
    }

    /**
     * Draws a new fractal image using the given image line data.
     */
    public void drawImage(List<Line> lines) {
        mandelPanel.clear();
        for (Line line : lines) {
            mandelPanel.draw(line);
        }
        mandelPanel.finish();
    }

    private void initComponents() {
        mandelPanel = new MandelPanel();
        rubberBandSelector = new RubberBandSelector(mandelPanel);

        setJMenuBar(createMenuBar());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Mandel");
        setLayout(new BorderLayout());

        add(mandelPanel, BorderLayout.CENTER);

        pack();
    }

    private JMenuBar createMenuBar() {
        // Menu bar
        JMenu fileMenu = new JMenu();
        fileMenu.setText("File");
        fileMenu.setMnemonic('F');

        newMenuItem = new JMenuItem();
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newMenuItem.setText("New");
        fileMenu.add(newMenuItem);

        exitMenuItem = new JMenuItem();
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        JMenu editMenu = new JMenu();
        editMenu.setText("Edit");
        editMenu.setMnemonic('E');

        undoMenuItem = new JMenuItem();
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoMenuItem.setText("Undo");
        editMenu.add(undoMenuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }
}
