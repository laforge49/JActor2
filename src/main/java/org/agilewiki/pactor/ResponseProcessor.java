package org.agilewiki.pactor;

abstract public class ResponseProcessor<RESPONSE_TYPE>
        implements ResponseProcessorInterface<RESPONSE_TYPE> {
    abstract public void processResponse(RESPONSE_TYPE response)
            throws Throwable;

    final public boolean responseRequired() {
        return true;
    }
}
