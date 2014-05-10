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
package org.agilewiki.jactor2.core.impl;

import java.util.Timer;
import java.util.TimerTask;

import org.agilewiki.jactor2.core.plant.PlantScheduler;

import com.blockwithme.util.base.SystemUtils;

/**
 * This class implements a PlantScheduler for coreSt.
 *
 * @author monster
 */
public class DefaultPlantScheduler extends Timer implements PlantScheduler {
    /** Simple wrapper for the Runnables. */
    private static class MyTimerTask extends TimerTask {
        private final Runnable runnable;

        public MyTimerTask(final Runnable _runnable) {
            if (_runnable == null) {
                throw new NullPointerException("_runnable");
            }
            runnable = _runnable;
        }

        /* (non-Javadoc)
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            runnable.run();
        }
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#scheduleAtFixedRate(java.lang.Runnable, long)
     */
    @Override
    public Object scheduleAtFixedRate(final Runnable _runnable,
            final int _millisecondDelay) {
        final MyTimerTask result = new MyTimerTask(_runnable);
        super.scheduleAtFixedRate(result, _millisecondDelay, _millisecondDelay);
        return result;
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#schedule(java.lang.Runnable, long)
     */
    @Override
    public Object schedule(final Runnable _runnable, final int _millisecondDelay) {
        final MyTimerTask result = new MyTimerTask(_runnable);
        super.schedule(result, _millisecondDelay);
        return result;
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#cancel(java.lang.Object)
     */
    @Override
    public void cancel(final Object task) {
        ((MyTimerTask) task).cancel();
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#close()
     */
    @Override
    public void close() {
        cancel();
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.plant.PlantScheduler#currentTimeMillis()
     */
    @Override
    public double currentTimeMillis() {
        return SystemUtils.currentTimeMillis();
    }
}
