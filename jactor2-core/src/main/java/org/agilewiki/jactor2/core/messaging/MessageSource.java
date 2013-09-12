package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.Reactor;

/**
 * A source of messages, and which must be able to handle a response.
 */
public interface MessageSource {

    /**
     * Process an incoming response.
     *
     * @param message        The response.
     * @param responseSource The reactor returning the response.
     */
    void incomingResponse(final Message message, final Reactor responseSource);
}
