package org.agilewiki.pactor;

import org.agilewiki.pactor.impl.ResponseProcessorInterface;

abstract public class ResponseProcessorProcessor<RESPONSE_TYPE>
        implements ResponseProcessorInterface<RESPONSE_TYPE> {
    abstract public void processResponse(RESPONSE_TYPE response)
            throws Exception;

    final public boolean responseRequired() {
        return true;
    }
}
