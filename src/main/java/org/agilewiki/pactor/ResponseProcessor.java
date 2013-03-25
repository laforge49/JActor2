package org.agilewiki.pactor;

public interface ResponseProcessor<RESPONSE_TYPE> {
    public void processResponse(final RESPONSE_TYPE response)
            throws Exception;
}
