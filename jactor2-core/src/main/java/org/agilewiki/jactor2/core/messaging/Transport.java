package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.context.JAContext;

/**
 * A callback passed to the Request.processRequest method.
 *
 * @param <RESPONSE_TYPE> The type of response.
 */
public interface Transport<RESPONSE_TYPE> extends ResponseProcessor<RESPONSE_TYPE> {
    /**
     * Returns the JAContext of the request source.
     *
     * @return The JAContext of the request source, or null when the request was
     *         passed using signal or call.
     */
    JAContext getMailboxFactory();

    /**
     * Returns an exception as a response instead of throwing it.
     * But regardless of how a response is returned, if the response is an exception it
     * is passed to the exception handler of the actor that did the call or send on the request.
     *
     * @param response An exception.
     */
    public void processException(final Exception response) throws Exception;
}
