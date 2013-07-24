package org.agilewiki.jactor2.api;

/**
 * A source of messages, which must be able to handle a response.
 */
public interface MessageSource {

    /**
     * Process an incoming response.
     */
    void incomingResponse(final Message message, final Mailbox responseSource);
}
