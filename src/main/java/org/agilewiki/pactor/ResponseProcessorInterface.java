package org.agilewiki.pactor;

public interface ResponseProcessorInterface<RESPONSE_TYPE> {
    void processResponse(final RESPONSE_TYPE response) throws Exception;

    boolean responseRequired();
}
