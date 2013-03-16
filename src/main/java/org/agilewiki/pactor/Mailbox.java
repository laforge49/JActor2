package org.agilewiki.pactor;

public interface Mailbox {
    Mailbox createMailbox();

    void addAutoClosable(final AutoCloseable closeable);

    void shutdown();

    void send(final Request<?> request) throws Exception;

    <E> void reply(final Request<E> request, final Mailbox source,
            final ResponseProcessorInterface<E> responseProcessor)
            throws Exception;

    <E> E pend(final Request<E> request) throws Exception;

    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);
}
