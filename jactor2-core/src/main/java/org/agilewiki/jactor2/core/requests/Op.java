package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.util.GwtIncompatible;
import org.agilewiki.jactor2.core.util.Timer;

/**
 * An operation, synchronous or asynchronous, optionally used to define requests.
 */
public interface Op<RESPONSE_TYPE> {
    /**
     * Send a request as a one-way message, with no message buffering.
     */
    void signal();

    /**
     * Send a request and wait for the response.
     *
     * @return The result value.
     */
    @GwtIncompatible
    RESPONSE_TYPE call() throws Exception;

    /**
     * Returns the Timer used to track the performance of this Request instance.
     *
     * Null is not allowed are return value, but Timer.NOP can be used to disable tracking.
     *
     * @return the Timer used to track the performance of this Request instance.
     */
    Timer getTimer();
}
