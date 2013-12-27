package org.agilewiki.jactor2.core.impl;

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
    void incomingResponse(final RequestImpl message, final ReactorImpl responseSource);
}
