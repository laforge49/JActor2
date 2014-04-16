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
package org.agilewiki.jactor2.core.impl.jvm;

import org.agilewiki.jactor2.core.plant.PlantScheduler;
import org.agilewiki.jactor2.core.util.GwtIncompatible;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * The JVM (non-GWT) single-threaded PlantScheduler implementation.
 *
 * @author monster
 */
@GwtIncompatible
public class JVMPlantScheduler implements PlantScheduler {

    /** Our Task implementation. */
    private final class Task implements Comparable<Task> {
        /** Are we cancelled? */
        boolean cancelled;

        /** Should we run only once? */
        boolean onceOnly;

        /** The real task. */
        Runnable task;

        /** When are we running again? */
        private long nextRun;

        /** Delay in MS. Only for repeating tasks. */
        private final long delay;

        /** Creates a Task */
        public Task(final Runnable task, final long delay) {
            this.delay = delay;
            this.task = task;
        }

        /** Executes the task. Returns true if it must be called again. */
        public boolean execute(final long now) {
            if (!cancelled) {
                task.run();
                if (onceOnly) {
                    cancelled = true;
                } else {
                    updateNextRun(now);
                }
            }
            return !cancelled;
        }

        /** Updates the next run. */
        public void updateNextRun(final long now) {
            nextRun = now + delay;
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(final Task o) {
            return (nextRun < o.nextRun) ? -1
                    : ((nextRun == o.nextRun) ? 0 : 1);
        }
    }

    /** The scheduled tasks. */
    private final TreeSet<Task> tasks = new TreeSet<>();

    /** The approximate current time. */
    private long now;

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#scheduleAtFixedRate(java.lang.Runnable, long)
     */
    @Override
    public Task scheduleAtFixedRate(final Runnable _runnable,
            final long _millisecondDelay) {
        if (_runnable == null) {
            throw new NullPointerException("_runnable");
        }
        if ((_millisecondDelay < 1) || (_millisecondDelay > Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("_millisecondDelay: "
                    + _millisecondDelay);
        }
        final Task result = new Task(_runnable, _millisecondDelay);
        result.updateNextRun(now);
        tasks.add(result);
        return result;
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#schedule(java.lang.Runnable, long)
     */
    @Override
    public Task schedule(final Runnable _runnable, final long _millisecondDelay) {
        final Task result = scheduleAtFixedRate(_runnable, _millisecondDelay);
        result.onceOnly = true;
        return result;
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#cancel(java.lang.Object)
     */
    @Override
    public void cancel(final Object task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (tasks.remove(task)) {
            final Task cmd = (Task) task;
            cmd.cancelled = true;
            cmd.task = null;
        } else if (!(task instanceof Task)) {
            throw new IllegalArgumentException("Not a task: " + task.getClass());
        }
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#currentTimeMillis()
     */
    @Override
    public long currentTimeMillis() {
        return now;
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#close()
     */
    @Override
    public void close() {
        for (final Task cmd : tasks) {
            cmd.cancelled = true;
            cmd.task = null;
        }
        tasks.clear();
    }

    /**
     * Run the tasks that need to run now.
     *
     * @param now the current time.
     * @return the next time it should be called. 0 for "nothing else to do".
     */
    public long update(final long now) {
        this.now = now;
        long result = 0;
        if (!tasks.isEmpty()) {
            final long nextRun = tasks.first().nextRun;
            if (nextRun <= now) {
                ArrayList<Task> readd = null;
                final Task[] array = tasks.toArray(new Task[tasks.size()]);
                for (final Task task : array) {
                    if (task.nextRun > now) {
                        result = task.nextRun;
                        break;
                    }
                    // Must remove *before* calling execute()
                    tasks.remove(task);
                    if (task.execute(now)) {
                        if (readd == null) {
                            readd = new ArrayList<>();
                        }
                        readd.add(task);
                    }
                }
                if (readd != null) {
                    tasks.addAll(readd);
                }
            } else {
                result = nextRun;
            }
        }
        return result;
    }
}
