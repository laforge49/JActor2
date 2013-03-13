package org.agilewiki.pactor;

import org.agilewiki.pactor.impl.ProcessResponseInterface;

abstract public class ResponseProcessor<RESPONSE_TYPE>
        implements ProcessResponseInterface<RESPONSE_TYPE> {
    abstract public void processResponse(RESPONSE_TYPE response)
            throws Exception;

    final public boolean responseRequired() {
        return true;
    }
}
