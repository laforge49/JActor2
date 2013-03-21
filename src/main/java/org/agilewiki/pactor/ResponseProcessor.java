package org.agilewiki.pactor;

public interface ResponseProcessor<RESPONSE_TYPE> {
    public abstract void processResponse(final RESPONSE_TYPE response)
            throws Exception;
}
