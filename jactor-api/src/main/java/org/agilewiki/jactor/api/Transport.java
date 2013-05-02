package org.agilewiki.jactor.api;

/**
 * A callback passed to the _Request.processRequest method.
 *
 * @param <RESPONSE_TYPE> The type of response.
 */
public interface Transport<RESPONSE_TYPE> extends ResponseProcessor<RESPONSE_TYPE> {
    /**
     * Returns the MailboxFactory of the request source.
     *
     * @return The MailboxFactory of the request source, or null when the request was
     *         passed using signal or call.
     */
    MailboxFactory getMailboxFactory();
}
