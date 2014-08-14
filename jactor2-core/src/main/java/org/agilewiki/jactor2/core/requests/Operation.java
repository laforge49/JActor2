package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.util.Timer;

/**
 * An operation, synchronous or asynchronous, optionally used to define requests.
 */
public interface Operation<RESPONSE_TYPE> {
    /**
     * Returns the Timer used to track the performance of this Request instance.
     *
     * Null is not allowed as return value, but Timer.NOP can be used to disable tracking.
     *
     * @return the Timer used to track the performance of this Request instance.
     */
    Timer getTimer();
}
