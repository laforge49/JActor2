package org.agilewiki.pactor;

public interface Mailbox {
    public Mailbox createMailbox();

    public void addAutoClosable(AutoCloseable closeable);

    public void shutdown();

    public void send(Request request) throws Throwable;

    public void reply(Request request, Mailbox source, ResponseProcessorInterface responseProcessor)
            throws Throwable;

    public Object pend(Request request) throws Throwable;

    public ExceptionHandler setExceptionHandler(ExceptionHandler exceptionHandler);
}
