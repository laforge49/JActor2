package org.agilewiki.pactor.impl;

public interface ResponseProcessorInterface<RESPONSE_TYPE> {
    public void processResponse(RESPONSE_TYPE response)
            throws Exception;

    public boolean responseRequired();
}
