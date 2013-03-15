package org.agilewiki.pactor.impl;

interface MessageSource {
    public void incomingResponse(RequestMessage requestMessage, Object response);
}
