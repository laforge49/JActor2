package org.agilewiki.pactor;

public interface Mailbox {
    public Mailbox createMailbox();

    public void addAutoClosable(AutoCloseable closeable);

    public void shutdown();

    public void send(Request request) throws Exception;

    public void send(Request request, Mailbox source, ResponseProcessorInterface responseProcessor)
            throws Exception;

    public Object pend(Request request) throws Throwable;

    public ExceptionHandler setExceptionHandler(ExceptionHandler exceptionHandler);
}
