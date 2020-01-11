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

import se.dykstrom.jppf.mandel.view.MandelView;

import javax.swing.*;

/**
 * This is the main class of the Mandelbrot application.
 *
 * @author Johan Dykstrom
 */
class Mandel {

    public static void main(String[] args) throws Exception {
        int numberOfJobs = args.length > 0 ? Integer.parseInt(args[0]) : 1;

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(() -> {
            MandelView view = new MandelView();
            new MandelController(view, numberOfJobs);
            view.setVisible(true);
            view.setLocationRelativeTo(null);
        });
    }
}
