package org.agilewiki.jactor2.core.impl.stPlant;

import com.blockwithme.util.shared.SystemUtils;
import org.agilewiki.jactor2.core.plant.PlantScheduler;

import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A scheduler for Plant, created by PlantConfiguration.
 */
public class DefaultPlantScheduler implements PlantScheduler {

    @SuppressWarnings("rawtypes")
    private class MyTimerTask extends MyAbstractTimerTask {
        private volatile Runnable runnable;
        private volatile boolean cancelled;
        private volatile boolean done;
        private final boolean once;

        public MyTimerTask(final Runnable runnable, final boolean once) {
            this.runnable = runnable;
            this.once = once;
        }

        /* (non-Javadoc)
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            if (die) {
                cancel();
                runnable = null;
                timer.purge();
            } else {
                if (once) {
                    done = true;
                }
                runnable.run();
            }
        }

        /* (non-Javadoc)
         * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
         */
        @Override
        public long getDelay(final TimeUnit unit) {
            return unit.convert(
                    scheduledExecutionTime() - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS);
        }

        @Override
        public boolean cancel() {
            cancelled = true;
            return super.cancel();
        }

        /* (non-Javadoc)
         * @see java.util.concurrent.Future#isCancelled()
         */
        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        /* (non-Javadoc)
         * @see java.util.concurrent.Future#cancel(boolean)
         */
        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            return cancel();
        }

        /* (non-Javadoc)
         * @see java.util.concurrent.Future#isDone()
         */
        @Override
        public boolean isDone() {
            return done;
        }

        /* (non-Javadoc)
         * @see java.util.concurrent.Future#get()
         */
        @Override
        public Object get() throws InterruptedException, ExecutionException {
            if (done) {
                return null;
            }
            throw new InterruptedException();
        }

        /* (non-Javadoc)
         * @see java.util.concurrent.Future#get(long, java.util.concurrent.TimeUnit)
         */
        @Override
        public Object get(final long timeout, final TimeUnit unit)
                throws InterruptedException, ExecutionException,
                TimeoutException {
            if (done) {
                return null;
            }
            throw new InterruptedException();
        }
    }

    private volatile long currentTimeMillis;

    private volatile boolean die;

    private final Timer timer;

    /**
     * Creates the default plantScheduler.
     */
    public DefaultPlantScheduler() {
        timer = SystemUtils.getTimer();
        currentTimeMillis = System.currentTimeMillis();
        timer.scheduleAtFixedRate(new MyTimerTask(new Runnable() {
            @Override
            public void run() {
                currentTimeMillis = System.currentTimeMillis();
            }
        }, false), getHeartbeatMillis(), getHeartbeatMillis());
    }

    /**
     * Controls how often currentTimeMillis is updated: every 500 milliseconds.
     *
     * @return The number of milliseconds between updates to currentTimeMillis.
     */
    protected long getHeartbeatMillis() {
        return 500;
    }

    /**
     * Determines the size of the scheduledThreadPool: 2.
     *
     * @return Returns the number of threads in the scheduledThreadPool.
     */
    protected int getSchedulerPoolSize() {
        return 1;
    }

    @Override
    public long currentTimeMillis() {
        return currentTimeMillis;
    }

    @Override
    public ScheduledFuture<?> schedule(final Runnable runnable,
                                       final long _millisecondDelay) {
        final MyTimerTask result = new MyTimerTask(runnable, true);
        timer.schedule(result, _millisecondDelay);
        return result;
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable runnable,
                                                  final long _millisecondDelay) {
        final MyTimerTask result = new MyTimerTask(runnable, false);
        timer.scheduleAtFixedRate(result, _millisecondDelay, _millisecondDelay);
        return result;
    }

    @Override
    public void close() {
        // No way to get the tasks from the Timer. :(
        die = true;
    }
}
