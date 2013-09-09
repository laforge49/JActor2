package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessorBase;
import org.agilewiki.jactor2.core.threading.ModuleContext;

import java.util.concurrent.Semaphore;

/**
 * AsyncRequest instances are used for passing both 1-way and 2-way buffered messages between actors.
 * Requests are typically created as an anonymous class within the targeted Actor and are bound
 * to that actor's message processor.
 * The signal (1-way messaging) and call method (2-way messaging) pass unbuffered messages
 * to the target actor immediately, where they are enqueued for processing. But rather than being
 * sent immediately, request messages passed using the send method
 * (2-way messages) and all response messages are buffered for improved throughput. The send method
 * also supports thread migration.
 * <p>
 * A request also serves as a message and can only be used once.
 * So member variables of anonymous subclasses can be used to hold intermediate state when
 * when processing a request. This makes it easier to keep requests atomic.
 * </p>
 * <p>
 * Some care needs to be taken with the parameters passed to the target actor when creating a
 * AsyncRequest. The application must take care not to change the contents of these parameters,
 * as they will likely be accessed from a different thread when the target actor
 * is operated on.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 *
 * import org.agilewiki.jactor2.core.ActorBase;
 * import org.agilewiki.jactor2.core.context.ModuleContext;
 * import org.agilewiki.jactor2.core.processing.MessageProcessor;
 * import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
 *
 * public class RequestSample {
 *
 *     public static void main(String[] args) throws Exception {
 *
 *         //A context with two threads.
 *         final ModuleContext moduleContext = new ModuleContext(2);
 *
 *         try {
 *
 *             //Create actorA.
 *             SampleActor2 actorA = new SampleActor2(new NonBlockingMessageProcessor(moduleContext));
 *
 *             //Initialize actorA to 1.
 *             actorA.updateAReq(1).signal();
 *
 *             //Change actorA to 2.
 *             System.out.println("was " + actorA.updateAReq(2).call() + " but is now 2");
 *
 *             //Create actorB with a reference to actorA.
 *             IndirectActor actorB = new IndirectActor(actorA, new NonBlockingMessageProcessor(moduleContext));
 *
 *             //Indirectly change actorA to 42.
 *             System.out.println("was " + actorB.indirectAReq(42).call() + " but is now 42");
 *
 *         } finally {
 *             //shutdown the context
 *             moduleContext.close();
 *         }
 *
 *     }
 *
 * }
 *
 * //A simple actor with state.
 * class SampleActor2 extends ActorBase {
 *
 *     //Initial state is 0.
 *     private int state = 0;
 *
 *     //Create a SimpleActor2.
 *     SampleActor2(final MessageProcessor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Return an update request.
 *     AsyncRequest&lt;Integer&gt; updateAReq(final int _newState) {
 *         return new AsyncRequest&lt;Integer&gt;(getMessageProcessor()) {
 *
 *             {@literal @}Override
 *             public void processAsyncRequest() throws Exception {
 *                 int oldState = state;
 *                 state = _newState; //assign the new state
 *                 processAsyncResponse(oldState); //return the old state.
 *             }
 *         };
 *     }
 *
 * }
 *
 * //An actor which operates on another actor.
 * class IndirectActor extends ActorBase {
 *
 *     //The other actor.
 *     private final SampleActor2 actorA;
 *
 *     //Create an IndirectActor with a reference to another actor.
 *     IndirectActor(final SampleActor2 _actorA, final MessageProcessor _messageProcessor) throws Exception {
 *         actorA = _actorA;
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Return a request to update the other actor and return its new state.
 *     AsyncRequest&lt;Integer&gt; indirectAReq(final int _newState) {
 *         return new AsyncRequest&lt;Integer&gt;(getMessageProcessor()) {
 *             AsyncRequest<Integer> dis = this;
 *
 *             {@literal @}Override
 *             public void processAsyncRequest() throws Exception {
 *
 *                 //Get a request from the other actor.
 *                 AsyncRequest&lt;Integer&gt; req = actorA.updateAReq(_newState);
 *
 *                 //Send the request to the other actor.
 *                 req.send(getMessageProcessor(), new AsyncResponseProcessor&lt;Integer&gt;() {
 *
 *                     {@literal @}Override
 *                     public void processAsyncResponse(Integer response) throws Exception {
 *
 *                         //Return the old state.
 *                         dis.processAsyncResponse(response);
 *                     }
 *                 });
 *             }
 *         };
 *     }
 * }
 *
 * Output:
 * was 1 but is now 2
 * was 2 but is now 42
 * </pre>
 *
 * @param <RESPONSE_TYPE> The class of the result returned after the AsyncRequest operates on the target actor.
 */
public abstract class AsyncRequest<RESPONSE_TYPE>
        extends RequestBase<RESPONSE_TYPE>
        implements AsyncResponseProcessor<RESPONSE_TYPE> {

    /**
     * Create an AsyncRequest and bind it to its target message processor.
     *
     * @param _targetMessageProcessor The message processor where this AsyncRequest Objects is passed for processing.
     *                                The thread owned by this message processor will process this AsyncRequest.
     */
    public AsyncRequest(final MessageProcessor _targetMessageProcessor) {
        super(_targetMessageProcessor);
    }

    /**
     * The processAsyncRequest method will be invoked by the target MessageProcessor on its own thread
     * when the AsyncRequest is dequeued from the target inbox for processing.
     */
    abstract public void processAsyncRequest()
            throws Exception;

    @Override
    public void processAsyncResponse(final RESPONSE_TYPE _response)
            throws Exception {
        processObjectResponse(_response);
    }

    /**
     * The processAsyncResponse method accepts the response of a request.
     * <p>
     * This method need not be thread-safe, as it
     * is always invoked from the same light-weight thread (message processor) that passed the
     * AsyncRequest and AsyncResponseProcessor objects.
     * </p>
     *
     * @param _response The response to a request.
     * @return True when this is the first response.
     */
    public boolean processCheckAsyncResponse(final RESPONSE_TYPE _response)
            throws Exception {
        return processObjectResponse(_response);
    }

    /**
     * Returns an exception as a response instead of throwing it.
     * But regardless of how a response is returned, if the response is an exception it
     * is passed to the exception handler of the actor that did the call or send on the request.
     *
     * @param _response An exception.
     */
    public void processAsyncException(final Exception _response) throws Exception {
        processObjectResponse(_response);
    }

    @Override
    protected void processRequestMessage() {
        final ModuleContext moduleContext = messageProcessor.getModuleContext();
        if (foreign)
            moduleContext.addAutoClosable(this);
        messageProcessor.setExceptionHandler(null);
        messageProcessor.setCurrentMessage(this);
        messageProcessor.requestBegin();
        try {
            processAsyncRequest();
        } catch (final Throwable t) {
            if (foreign)
                moduleContext.removeAutoClosable(this);
            processThrowable(messageProcessor, t);
        }
    }
}
