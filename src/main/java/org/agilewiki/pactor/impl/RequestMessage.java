package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessorInterface;

public final class RequestMessage implements Message {
    private MailboxImpl sourceMailbox;
    private MailboxImpl destinationMailbox;
    private Request request;
    private ExceptionHandler sourceExceptionHandler;
    private ResponseProcessorInterface responseProcessor;
    private boolean active = true;

    public RequestMessage(
            MailboxImpl sourceMailbox,
            MailboxImpl destinationMailbox,
            Request request,
            ExceptionHandler sourceExceptionHandler,
            ResponseProcessorInterface responseProcessor) {
        this.sourceMailbox = sourceMailbox;
        this.destinationMailbox = destinationMailbox;
        this.request = request;
        this.sourceExceptionHandler = sourceExceptionHandler;
        this.responseProcessor = responseProcessor;
    }
    //todo
}
