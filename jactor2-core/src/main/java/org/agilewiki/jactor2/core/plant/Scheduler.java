package org.agilewiki.jactor2.core.plant;

public interface Scheduler {
    void schedule(Runnable runnable, long _millisecondDelay);
    void scheduleAtFixedRate(Runnable runnable, long _millisecondDelay);
    long currentTimeMillis();
    void close();
}
