package org.agilewiki.jactor2.core.messages;

/**
 * Handles an exception within the context of processing an async request.
 *
 * @param <RESPONSE_TYPE>    The type of response passed back by the async request.
 */
abstract public class ExceptionHandler<RESPONSE_TYPE> {
    /**
     * Process an exception or rethrow it.
     *
     * @param e The exception to be processed.
     */
    public RESPONSE_TYPE processException(final Exception e) throws Exception {
        throw e;
    }

    /**
     * Process an exception or rethrow it.
     * By default, processException(Exception) is called.
     *
     * @param e                          The exception to be processed.
     * @param _asyncResponseProcessor    The response processor for passing back a result.
     */
    public void processException(final Exception e,
            final AsyncResponseProcessor<RESPONSE_TYPE> _asyncResponseProcessor)
            throws Exception {
        _asyncResponseProcessor.processAsyncResponse(processException(e));
    }
}
