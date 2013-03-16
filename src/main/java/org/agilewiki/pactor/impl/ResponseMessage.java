package org.agilewiki.pactor.impl;

final class ResponseMessage implements Message {
    private final RequestMessage requestMessage;
    private final Object response;

    public ResponseMessage(final RequestMessage request, final Object _response) {
        this.requestMessage = request;
        this.response = _response;
    }

    /**
     * @return the requestMessage
     */
    public RequestMessage getRequestMessage() {
        return requestMessage;
    }

    /**
     * @return the response
     */
    public Object getResponse() {
        return response;
    }
}
