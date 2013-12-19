package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.reactors.Reactor;

public interface Request<RESPONSE_TYPE> {
    RequestImpl<RESPONSE_TYPE> asRequestImpl();
    Reactor getTargetReactor();
    RESPONSE_TYPE call() throws Exception;

    /**
     * Passes this Request to the target Reactor without any result being passed back.
     * I.E. The signal method results in a 1-way message being passed.
     * If an exception is thrown while processing this Request,
     * that exception is simply logged as a warning.
     */
    public void signal() throws Exception;
}
