package org.agilewiki.pactor.util;

import org.agilewiki.pactor.api.ResponseProcessor;

/**
 * The JAIterator.process method is called repeatedly until it returns a non-null response,
 * which is then returned by JAIterator.
 */
abstract public class PAIterator {
    /**
     * Iterates over the process method until there is a non-null result.
     *
     * @param responseProcessor The response processor.
     */
    public void iterate(final ResponseProcessor responseProcessor)
            throws Exception {
        final ResponseProcessor erp = new ResponseProcessor() {
            @Override
            public void processResponse(final Object response) throws Exception {
                if (response == null) {
                    iterate(responseProcessor); //not recursive
                }/* else if (response instanceof JANull)
                    responseProcessor.processResponse(null);*/ else
                    responseProcessor.processResponse(response);
            }
        };
        process(erp);
    }

    /**
     * Perform an iteration.
     *
     * @param responseProcessor Processes the response.
     */
    abstract protected void process(final ResponseProcessor responseProcessor)
            throws Exception;
}
