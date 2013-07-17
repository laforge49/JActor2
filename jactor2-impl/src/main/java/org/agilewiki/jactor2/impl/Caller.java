package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.Message;
import org.agilewiki.jactor2.api.MessageSource;

import java.util.concurrent.Semaphore;

/**
 * Waits for an incoming response.
 */
final class Caller implements MessageSource {

    /**
     * Used to signal the arrival of a response.
     */
    private final Semaphore done = new Semaphore(0);

    /**
     * The result from the incoming response. May be null or an Exception.
     */
    private transient Object result;

    /**
     * Returns the response, which may be null. But if the response
     * is an exception, then it is thrown.
     *
     * @return The response or null, but not an exception.
     */
    public Object call() throws Exception {
        done.acquire();
        if (result instanceof Exception)
            throw (Exception) result;
        if (result instanceof Error)
            throw (Error) result;
        return result;
    }

    @Override
    public void incomingResponse(final Message _message,
                                 final Mailbox _responseSource) {
        this.result = _message.getResponse();
        done.release();
    }

    @Override
    public boolean buffer(final Message _message, final Mailbox _target) {
        return false;
    }
}
