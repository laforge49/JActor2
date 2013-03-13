package org.agilewiki.pactor.impl;

public interface MessageSource {
    public void incomingResponse(RequestMessage requestMessage, Object response);
}
