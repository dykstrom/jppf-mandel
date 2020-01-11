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

import java.util.Timer;
import java.util.TimerTask;

/**
 * Debounces the configured action to make sure it is not called too frequently.
 *
 * @author Johan Dykstrom
 */
public class Debouncer implements Runnable {

    private final Runnable action;
    private final long delay;

    private final Timer timer = new Timer(true);
    private TimerTask task = createTask();

    Debouncer(Runnable action) {
        this(action, 100);
    }

    Debouncer(Runnable action, long delay) {
        this.action = action;
        this.delay = delay;
    }

    @Override
    public void run() {
        task.cancel();
        task = createTask();
        timer.schedule(task, delay);
    }

    private TimerTask createTask() {
        return new TimerTask() {
            @Override
            public void run() {
                action.run();
            }
        };
    }
}
