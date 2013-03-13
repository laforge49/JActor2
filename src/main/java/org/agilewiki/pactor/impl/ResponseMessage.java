package org.agilewiki.pactor.impl;

public final class ResponseMessage implements Message {
    public RequestMessage requestMessage;
    public Object response;

    public ResponseMessage(RequestMessage requestMessage, Object response) {
        this.requestMessage = requestMessage;
        this.response = response;
    }
}
