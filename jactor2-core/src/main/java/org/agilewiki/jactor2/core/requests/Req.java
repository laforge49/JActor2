package org.agilewiki.jactor2.core.requests;

/**
 * A no-boilerplate request.
 */
public interface Req<RESPONSE_TYPE> {
    /**
     * Send a request as a one-way message, with no message buffering.
     */
    public void signal();

    /**
     * Send a request and wait for the response.
     *
     * @return The result value.
     */
    public RESPONSE_TYPE call() throws Exception;
}
