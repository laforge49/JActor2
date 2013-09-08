package org.agilewiki.jactor2.core.messaging;

/**
 * Exception handlers are used to alter how exceptions are processed.
 * <p>
 * By default, an exception which occurs while processing a call or send request is
 * returned as a result to the source processing or caller.
 * And for 1-way messages, the default is to simply log the exception as a warning.
 * Exception processing is specific to the request/event message being processed.
 * An application can set the exception handler for the request/event currently being processed using the
 * MessageProcessor.setExceptionHandler method.
 * </p>
 * <p>
 * When a message processor receives an exception as a result, the exception is handled the same way as any other
 * exception, by either passing it to an exception handler or returning it to the source of the request
 * being processed. On the other hand when a caller receives an exception as a result, the exception is
 * simply rethrown rather than passing it to the application logic as a response.
 * </p>
 * <p>
 * An exception handler can be selective as to which exceptions will be handled.
 * Any exceptions that it does not handle are simply rethrown, with default exception handling then
 * processing the exception.
 * Exception handlers can also return a result, providing they have access to the appropriate
 * ResponseProcessor object.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * import org.agilewiki.jactor2.core.ActorBase;
 * import org.agilewiki.jactor2.core.context.ModuleContext;
 * import org.agilewiki.jactor2.core.processing.MessageProcessor;
 * import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
 *
 * public class ExceptionHandlerSample {
 *
 *     public static void main(final String[] _args) throws Exception {
 *
 *         //A context with two threads.
 *         final ModuleContext moduleContext = new ModuleContext(2);
 *
 *         try {
 *
 *             //Create an ExceptionActor.
 *             ExceptionActor exceptionActor = new ExceptionActor(new NonBlockingMessageProcessor(moduleContext));
 *
 *             try {
 *                 //Create and call an exception request.
 *                 exceptionActor.exceptionReq().call();
 *                 System.out.println("can not get here");
 *             } catch (IllegalStateException ise) {
 *                 System.out.println("got first IllegalStateException, as expected");
 *             }
 *
 *             //Create an ExceptionHandlerActor.
 *             ExceptionHandlerActor exceptionHandlerActor =
 *                     new ExceptionHandlerActor(exceptionActor, new NonBlockingMessageProcessor(moduleContext));
 *             //Create a test request, call it and print the results.
 *             System.out.println(exceptionHandlerActor.testReq().call());
 *
 *         } finally {
 *             //shutdown the context
 *             moduleContext.close();
 *         }
 *     }
 * }
 *
 * //An actor with a request that throws an exception.
 * class ExceptionActor extends ActorBase {
 *
 *     //Create an ExceptionActor.
 *     ExceptionActor(final MessageProcessor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Returns an exception request.
 *     AsyncRequest&lt;Void&gt; exceptionReq() {
 *         return new AsyncRequest&lt;Void&gt;(getMessageProcessor()) {
 *
 *             {@literal @}Override
 *             public void processRequest() throws Exception {
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
 *     ExceptionHandlerActor(final ExceptionActor _exceptionActor, final MessageProcessor _messageProcessor) throws Exception {
 *         exceptionActor = _exceptionActor;
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Returns a test request.
 *     AsyncRequest&lt;String&gt; testReq() {
 *         return new AsyncRequest&lt;String&gt;(getMessageProcessor()) {
 *             AsyncRequest<String> dis = this;
 *
 *             {@literal @}Override
 *             public void processRequest() throws Exception {
 *
 *                 //Create and assign an exception handler.
 *                 getMessageProcessor().setExceptionHandler(new ExceptionHandler() {
 *                     {@literal @}Override
 *                     public void processException(final Throwable _throwable) throws Throwable {
 *                         if (_throwable instanceof IllegalStateException) {
 *                             //Returns a result if an IllegalStateException was thrown.
 *                             processResponse("got IllegalStateException, as expected");
 *                         } else //Otherwise rethrow the exception.
 *                             throw _throwable;
 *                     }
 *                 });
 *
 *                 //Create an exception request and send it to the exception actor for processing.
 *                 //The thrown exception is then caught by the assigned exception handler.
 *                 exceptionActor.exceptionReq().send(getMessageProcessor(), new ResponseProcessor&lt;Void&gt;() {
 *                     {@literal @}Override
 *                     public void processResponse(final Void _response) throws Exception {
 *                         dis.processResponse("can not get here");
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
public interface ExceptionHandler {
    /**
     * Process an exception or rethrow it.
     *
     * @param throwable The exception to be processed.
     */
    public abstract void processException(final Throwable throwable)
            throws Throwable;
}
