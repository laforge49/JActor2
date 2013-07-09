package org.agilewiki.jactor.api;

/**
 * A ResponseProcessor is either a callback sent with a _Request to another actor's mailbox
 * as a means of returning a response,
 * or a callback passed to the _Request.processRequest method.
 *
 * @param <RESPONSE_TYPE> The type of response.
 */
public interface ResponseProcessor<RESPONSE_TYPE> {
    /**
     * The processResponse method accepts the response of a request.
     * <p>
     * In the case of a ResponseProcessor object sent with a _Request,
     * this method need not be thread-safe, as it
     * is always invoked from the same light-weight thread (mailbox) that sent the
     * _Request and ResponseProcessor objects.
     * </p>
     *
     * @param response The response to a request.
     */
    public void processResponse(final RESPONSE_TYPE response) throws Exception;
}
