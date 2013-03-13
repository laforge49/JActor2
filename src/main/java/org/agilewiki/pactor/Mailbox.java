package org.agilewiki.pactor;

import org.agilewiki.pactor.impl.ResponseProcessorInterface;

public interface Mailbox {
    public Mailbox createMailbox();

    public void addAutoClosable(AutoCloseable closeable);

    public void shutdown();

    public void send(Request request) throws Exception;

    public void send(Request request, Mailbox source, ResponseProcessorInterface responseProcessor)
            throws Exception;

    public Object pend(Request request) throws Exception;

    public ExceptionHandler setExceptionHandler(ExceptionHandler exceptionHandler);
}
