package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessorInterface;

final class RequestMessage implements Message {
    public MessageSource messageSource;
    public RequestMessage oldRequestMessage;
    public Request request;
    public ExceptionHandler sourceExceptionHandler;
    public ResponseProcessorInterface responseProcessor;
    public boolean active = true;

    public RequestMessage(
            MessageSource messageSource,
            RequestMessage oldRequestMessage,
            Request request,
            ExceptionHandler sourceExceptionHandler,
            ResponseProcessorInterface responseProcessor) {
        this.messageSource = messageSource;
        this.oldRequestMessage = oldRequestMessage;
        this.request = request;
        this.sourceExceptionHandler = sourceExceptionHandler;
        this.responseProcessor = responseProcessor;
    }
}
