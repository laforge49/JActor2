package org.agilewiki.jactor2.api;

/**
 * A source of requests, which must be able to handle a response.
 */
public interface MessageSource {

    /**
     * Process an incoming response.
     */
    void incomingResponse(final RequestMessage message, final Mailbox responseSource);
}
