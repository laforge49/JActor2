package org.agilewiki.jactor2.core.messaging;

/**
 * Exception handlers are used to alter how exceptions are processed.
 * <p>
 * By default, an exception which occurs while processing a call or send request is
 * returned as a result to the source processing or caller.
 * And for 1-way messages, the default is to simply log the exception as a warning.
 * Exception processing is specific to the request/event message being processed.
 * An application can set the exception handler for the request/event currently being processed using the
 * Reactor.setExceptionHandler method.
 * </p>
 * <p>
 * When a reactor receives an exception as a result, the exception is handled the same way as any other
 * exception, by either passing it to an exception handler or returning it to the source of the request
 * being processed. On the other hand when a caller receives an exception as a result, the exception is
 * simply rethrown rather than passing it to the application logic as a response.
 * </p>
 * <p>
 * An exception handler can be selective as to which exceptions will be handled.
 * Any exceptions that it does not handle are simply rethrown, with default exception handling then
 * processing the exception.
 * Exception handlers can also return a result, providing they have access to the appropriate
 * AsyncResponseProcessor object.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * import org.agilewiki.jactor2.core.ActorBase;
 * import org.agilewiki.jactor2.core.threading.Facility;
 * import org.agilewiki.jactor2.core.processing.Reactor;
 * import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
 *
 * public class ExceptionHandlerSample {
 *
 *     public static void main(final String[] _args) throws Exception {
 *
 *         //A facility with two threads.
 *         final Facility facility = new Facility(2);
 *
 *         try {
 *
 *             //Create an ExceptionActor.
 *             ExceptionActor exceptionActor = new ExceptionActor(new NonBlockingReactor(facility));
 *
 *             try {
 *                 //Create and call an exception request.
 *                 exceptionActor.exceptionAReq().call();
 *                 System.out.println("can not get here");
 *             } catch (IllegalStateException ise) {
 *                 System.out.println("got first IllegalStateException, as expected");
 *             }
 *
 *             //Create an ExceptionHandlerActor.
 *             ExceptionHandlerActor exceptionHandlerActor =
 *                     new ExceptionHandlerActor(exceptionActor, new NonBlockingReactor(facility));
 *             //Create a test request, call it and print the results.
 *             System.out.println(exceptionHandlerActor.testAReq().call());
 *
 *         } finally {
 *             //shutdown the facility
 *             facility.close();
 *         }
 *     }
 * }
 *
 * //An actor with a request that throws an exception.
 * class ExceptionActor extends ActorBase {
 *
 *     //Create an ExceptionActor.
 *     ExceptionActor(final Reactor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Returns an exception request.
 *     AsyncRequest&lt;Void&gt; exceptionAReq() {
 *         return new AsyncRequest&lt;Void&gt;(getReactor()) {
 *
 *             {@literal @}Override
 *             public void processAsyncRequest() throws Exception {
 *                 throw new IllegalStateException(); //Throw an exception when the request is processed.
 *             }
 *         };
 *     }
 * }
 *
 * //An actor with an exception handler.
 * class ExceptionHandlerActor extends ActorBase {
 *
 *     //An actor with a request that throws an exception.
 *     private final ExceptionActor exceptionActor;
 *
 *     //Create an exception handler actor with a reference to an exception actor.
 *     ExceptionHandlerActor(final ExceptionActor _exceptionActor, final Reactor _messageProcessor) throws Exception {
 *         exceptionActor = _exceptionActor;
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Returns a test request.
 *     AsyncRequest&lt;String&gt; testAReq() {
 *         return new AsyncRequest&lt;String&gt;(getReactor()) {
 *             AsyncRequest&lt;String&gt; dis = this;
 *
 *             {@literal @}Override
 *             public void processAsyncRequest() throws Exception {
 *
 *                 //Create and assign an exception handler.
 *                 setExceptionHandler(new ExceptionHandler&lt;String&gt;() {
 *                     {@literal @}Override
 *                     public String processException(final Exception _exception) throws Exception {
 *                         if (_exception instanceof IllegalStateException) {
 *                             //Returns a result if an IllegalStateException was thrown.
 *                             return "got IllegalStateException, as expected";
 *                         } else //Otherwise rethrow the exception.
 *                             throw _exception;
 *                     }
 *                 });
 *
 *                 //Create an exception request and send it to the exception actor for processing.
 *                 //The thrown exception is then caught by the assigned exception handler.
 *                 exceptionActor.exceptionAReq().send(getReactor(), new AsyncResponseProcessor&lt;Void&gt;() {
 *                     {@literal @}Override
 *                     public void processAsyncResponse(final Void _response) throws Exception {
 *                         dis.processAsyncResponse("can not get here");
 *                     }
 *                 });
 *             }
 *         };
 *     }
 *
 * }
 *
 * Output:
 * got first IllegalStateException, as expected
 * got IllegalStateException, as expected
 * </pre>
 */
public interface ExceptionHandler<RESPONSE_TYPE> {
    /**
     * Process an exception or rethrow it.
     *
     * @param e The exception to be processed.
     */
    public abstract RESPONSE_TYPE processException(final Exception e)
            throws Exception;
}
