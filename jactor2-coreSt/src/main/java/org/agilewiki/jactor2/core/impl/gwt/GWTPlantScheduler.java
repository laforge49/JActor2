/*
 * Copyright (C) 2014 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agilewiki.jactor2.core.impl.gwt;

import org.agilewiki.jactor2.core.plant.PlantScheduler;

import com.blockwithme.util.shared.SystemUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

/**
 * This class implements a PlantScheduler for GWT.
 *
 * @author monster
 */
public class GWTPlantScheduler implements PlantScheduler {

    /** Our RepeatingCommand implementation, to represent tasks within the GWT Scheduler. */
    private class MyRepeatingCommand implements RepeatingCommand {
        /** Are we cancelled? */
        boolean cancelled;

        /** Should we run only once? */
        boolean onceOnly;

        /** The real task. */
        Runnable task;

        /* (non-Javadoc)
         * @see com.google.gwt.core.client.Scheduler.RepeatingCommand#execute()
         */
        @Override
        public boolean execute() {
            if (closed) {
                cancelled = true;
            }
            if (!cancelled) {
                task.run();
                if (onceOnly) {
                    cancel(this);
                }
            }
            return !cancelled;
        }

    }

    /** The real GWT scheduler. */
    private final Scheduler realScheduler = Scheduler.get();

    /** Are we closed? */
    private boolean closed;

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#scheduleAtFixedRate(java.lang.Runnable, long)
     */
    @Override
    public MyRepeatingCommand scheduleAtFixedRate(final Runnable _runnable,
            final long _millisecondDelay) {
        if (closed) {
            throw new IllegalStateException("Closed!");
        }
        if (_runnable == null) {
            throw new NullPointerException("_runnable");
        }
        if ((_millisecondDelay < 1) || (_millisecondDelay > Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("_millisecondDelay: "
                    + _millisecondDelay);
        }
        final MyRepeatingCommand result = new MyRepeatingCommand();
        result.task = _runnable;
        realScheduler.scheduleFixedPeriod(result, (int) _millisecondDelay);
        return result;
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#schedule(java.lang.Runnable, long)
     */
    @Override
    public MyRepeatingCommand schedule(final Runnable _runnable,
            final long _millisecondDelay) {
        final MyRepeatingCommand result = scheduleAtFixedRate(_runnable,
                _millisecondDelay);
        result.onceOnly = true;
        return result;
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#cancel(java.lang.Object)
     */
    @Override
    public void cancel(final Object task) {
        if (task instanceof MyRepeatingCommand) {
            final MyRepeatingCommand cmd = (MyRepeatingCommand) task;
            cmd.cancelled = true;
            cmd.task = null;
        } else if (task != null) {
            throw new IllegalArgumentException("Not a task: " + task.getClass());
        }
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#currentTimeMillis()
     */
    @Override
    public long currentTimeMillis() {
        return SystemUtils.currentTimeMillis();
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#close()
     */
    @Override
    public void close() {
        closed = true;
    }
}
