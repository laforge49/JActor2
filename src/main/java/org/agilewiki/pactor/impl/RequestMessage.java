package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessorInterface;

final class RequestMessage implements Message {
    private final MessageSource messageSource;
    private final RequestMessage oldRequestMessage;
    private final Request<?> request;
    private final ExceptionHandler sourceExceptionHandler;
    private final ResponseProcessorInterface<?> responseProcessor;
    private boolean active = true;

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param _active the active to set
     */
    public void setActive(final boolean _active) {
        this.active = _active;
    }

    /**
     * @return the messageSource
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * @return the oldRequestMessage
     */
    public RequestMessage getOldRequestMessage() {
        return oldRequestMessage;
    }

    /**
     * @return the request
     */
    public Request<?> getRequest() {
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
    public ResponseProcessorInterface<?> getResponseProcessor() {
        return responseProcessor;
    }

    public RequestMessage(final MessageSource source, final RequestMessage old,
            final Request<?> _request, final ExceptionHandler handler,
            final ResponseProcessorInterface<?> rp) {
        messageSource = source;
        oldRequestMessage = old;
        request = _request;
        sourceExceptionHandler = handler;
        responseProcessor = rp;
    }
}
