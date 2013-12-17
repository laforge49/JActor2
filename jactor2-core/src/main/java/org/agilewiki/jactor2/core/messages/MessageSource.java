package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;

/**
 * A source of messages, and which must be able to handle a response.
 */
public interface MessageSource {

    /**
     * Process an incoming response.
     *
     * @param message        The response.
     * @param responseSource The targetReactor returning the response.
     */
    void incomingResponse(final Message message, final ReactorImpl responseSource);
}
