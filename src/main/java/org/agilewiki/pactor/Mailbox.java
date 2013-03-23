package org.agilewiki.pactor;

public interface Mailbox {
    Mailbox createMailbox();

    void addAutoClosable(final AutoCloseable closeable);

    void shutdown();

    void send(final RequestBase<?> request) throws Exception;

    <E> void reply(final RequestBase<E> request, final Mailbox source,
            final ResponseProcessor<E> responseProcessor)
            throws Exception;

    <E> E pend(final RequestBase<E> request) throws Exception;

    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);
}
