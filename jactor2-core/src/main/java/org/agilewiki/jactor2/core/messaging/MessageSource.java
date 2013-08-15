package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * A source of messages, which must be able to handle a response.
 */
public interface MessageSource {

    /**
     * Process an incoming response.
     *
     * @param message        The response.
     * @param responseSource The processing returning the response.
     */
    void incomingResponse(final Message message, final MessageProcessor responseSource);
}
