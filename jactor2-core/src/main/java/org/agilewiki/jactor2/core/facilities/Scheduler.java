package org.agilewiki.jactor2.core.facilities;

public interface Scheduler {
    void schedule(Runnable runnable, long _millisecondDelay);
    long currentTimeMillis();
    void close();
}
