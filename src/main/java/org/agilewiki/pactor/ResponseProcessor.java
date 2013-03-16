package org.agilewiki.pactor;

public abstract class ResponseProcessor<RESPONSE_TYPE> implements
        ResponseProcessorInterface<RESPONSE_TYPE> {
    @Override
    public abstract void processResponse(final RESPONSE_TYPE response)
            throws Exception;

    @Override
    public final boolean responseRequired() {
        return true;
    }
}
