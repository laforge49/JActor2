package org.agilewiki.pactor;

/**
 * A callback sent with a _Request as a means of returning a response,
 * or a callback passed to the _Request.processRequest method.
 *
 * @param <RESPONSE_TYPE> the type of response.
 */
public interface ResponseProcessor<RESPONSE_TYPE> {
    /**
     * The processResponse method accepts the response of a request.
     * <p>
     * In the case of a ResponseProcessor object sent with a _Request,
     * this method need not be thread-safe, as it
     * is always invoked from the same light-weight thread that sent the
     * _Request and ResponseProcessor objects.
     * </p>
     *
     * @param response the response to a request.
     */
    public void processResponse(final RESPONSE_TYPE response)
            throws Exception;
}
