package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

class Message {
    private final MessageSource messageSource;
    private final Message oldMessage;
    private final RequestBase<?> request;
    private final ExceptionHandler sourceExceptionHandler;
    private final ResponseProcessor<?> responseProcessor;
    private boolean responsePending = true;
    private Object response;

    /**
     * @return the responsePending
     */
    public boolean isResponsePending() {
        return responsePending;
    }

    /**
     * @param _response the response being returned
     */
    public void setResponse(final Object _response) {
        responsePending = false;
        response = _response;
    }

    /**
     * @return the response
     */
    public Object getResponse() {
        return response;
    }

    /**
     * @return the messageSource
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * @return the oldMessage
     */
    public Message getOldMessage() {
        return oldMessage;
    }

    /**
     * @return the request
     */
    public RequestBase<?> getRequest() {
        return request;
    }

    /**
     * @return the sourceExceptionHandler
     */
    public ExceptionHandler getSourceExceptionHandler() {
        return sourceExceptionHandler;
    }

    /**
     * @return the responseProcessor
     */
    public ResponseProcessor<?> getResponseProcessor() {
        return responseProcessor;
    }

    public Message(final MessageSource source,
                   final Message old,
                   final RequestBase<?> _request,
                   final ExceptionHandler handler,
                   final ResponseProcessor<?> rp) {
        messageSource = source;
        oldMessage = old;
        request = _request;
        sourceExceptionHandler = handler;
        responseProcessor = rp;
    }
}
