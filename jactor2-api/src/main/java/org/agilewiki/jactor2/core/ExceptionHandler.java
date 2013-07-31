package org.agilewiki.jactor2.core;

/**
 * Exception handlers are used to alter how exceptions are processed.
 * <p>
 * By default, an exception which occurs while processing a call or send request is
 * returned as a result to the source mailbox or caller.
 * And for events (or a Request signal), the default is to simply log the exception as a warning.
 * Exception processing is specific to the request/event being processed.
 * An application can set the exception handler for the request/event currently being processed using the
 * Mailbox.setExceptionHandler method.
 * </p>
 * <p>
 * When a mailbox receives an exception as a result, the exception is handled the same way as any other
 * exception, by either passing it to an exception handler or returning it to the source of the request
 * being processed. On the other hand when a caller receives an exception as a result, the exception is
 * simply rethrown.
 * </p>
 * <p>
 * An exception handler can be selective in which classes of exceptions that it will handle.
 * Any exceptions that it does not handle are simply rethrown, with default exception handling then
 * processing the exception.
 * Exception handlers can also return a result.
 * </p>
 */
public interface ExceptionHandler {
    /**
     * Process an exception or rethrow it.
     *
     * @param throwable The exception to be processed.
     */
    public abstract void processException(final Throwable throwable)
            throws Throwable;
}
