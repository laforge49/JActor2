package org.agilewiki.pactor;

public interface ResponseProcessorInterface<RESPONSE_TYPE> {
    public void processResponse(RESPONSE_TYPE response)
            throws Throwable;

    public boolean responseRequired();
}
