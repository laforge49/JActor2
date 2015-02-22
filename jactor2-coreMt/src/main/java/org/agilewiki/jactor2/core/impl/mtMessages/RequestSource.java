package org.agilewiki.jactor2.core.impl.mtMessages;

import org.agilewiki.jactor2.core.messages.impl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

/**
 * A source of requests, and which must be able to handle a response.
 */
public interface RequestSource {

    /**
     * Process an incoming response.
     *
     * @param message        The response.
     * @param responseSource The targetReactor returning the response.
     */
    void incomingResponse(final RequestImpl<?> message,
            final ReactorImpl responseSource);
}
