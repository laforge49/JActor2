package org.agilewiki.jactor2.api;

/**
 * A ResponseProcessor is an application callback for a request.
 *
 * @param <RESPONSE_TYPE> The type of response.
 */
public interface ResponseProcessor<RESPONSE_TYPE> {
    /**
     * The processResponse method accepts the response of a request.
     * <p>
     * This method need not be thread-safe, as it
     * is always invoked from the same light-weight thread (mailbox) that passed the
     * Request and ResponseProcessor objects.
     * </p>
     *
     * @param response The response to a request.
     */
    public void processResponse(final RESPONSE_TYPE response) throws Exception;
}
