package org.agilewiki.pactor;

public interface ProcessResponseInterface<RESPONSE_TYPE> {
    public void processResponse(RESPONSE_TYPE response)
            throws Exception;

    public boolean responseRequired();
}
