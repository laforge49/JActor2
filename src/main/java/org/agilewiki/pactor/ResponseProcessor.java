package org.agilewiki.pactor;

abstract public class ResponseProcessor<RESPONSE_TYPE> {
    abstract public void processResponse(RESPONSE_TYPE response);

    public boolean responseRequired() {
        return true;
    }
}
