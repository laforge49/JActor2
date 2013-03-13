package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Request;

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

    }
    //todo
}
