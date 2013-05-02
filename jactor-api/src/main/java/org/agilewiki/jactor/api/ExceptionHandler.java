package org.agilewiki.jactor.api;

/**
 * Exception handlers are used to alter how exceptions are processed.
 * <p>
 * By default, an exception which occurs while processing a request is
 * returned as a result to the source mailbox or caller.
 * Exception processing is specific to the request being processed.
 * An application can set the exception handler for the request currently being processed using the
 * Mailbox.setExceptionHandler method.
 * (When a request is sent using a signal method, the default is to simply log the exception as a warning.)
 * </p>
 * <p>
 * When a mailbox receives an exception as a result, the exception is handled the same way as any other
 * exception, by either passing it to an exception handler or returning it to the source of the request
 * being processed. On the other hand when a caller receives an exception as a result, the exception is
 * simply rethrown.
 * </p>
 * <p>
 * An exception handler can be selective in which classes of exceptions that it will handle.
 * Any exceptions that it does not handle are simply rethrown and are then
 * returned as a result to the source mailbox or caller.
 * Exception handlers can also return a result.
 * </p>
 * <pre>
 * public Request&lt;byte[]&gt; readReq() {
 *     return new RequestBase&lt;byte[]&gt;(getMailbox()) {
 *         public void processRequest(final ResponseProcessor _rp) throws Exception {
 *             getMailbox().setExceptionHandler(new ExceptionHandler() {
 *                 public void processException(final Throwable _t) throws Throwable {
 *                     if (_t instanceof IOException)
 *                         _rp.processResponse(null);
 *                     else
 *                         throw _t;
 *                 }
 *             });
 *             .
 *             .
 *             .
 *         }
 *     };
 * }
 * </pre>
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
