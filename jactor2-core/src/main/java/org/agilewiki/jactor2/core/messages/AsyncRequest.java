package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * AsyncRequest instances are used for passing both 1-way and 2-way buffered messages between blades.
 * Requests are typically created as an anonymous class within the targeted Blade and are bound
 * to that blades's targetReactor.
 * The signal (1-way messaging) and call method (2-way messaging) pass unbuffered messages
 * to the target blades immediately, where they are enqueued for processing. But rather than being
 * sent immediately, request messages passed using the doSend method
 * (2-way messages) and all response messages are buffered for improved throughput. The doSend method
 * also supports thread migration.
 * <p>
 * A request also serves as a message and can only be used once.
 * So member variables of anonymous subclasses can be used to hold intermediate state when
 * when processing a request. This makes it easier to keep requests atomic.
 * </p>
 * <p>
 * Some care needs to be taken with the parameters passed to the target blades when creating a
 * AsyncRequest. The application must take care not to change the contents of these parameters,
 * as they will likely be accessed from a different thread when the target blades
 * is operated on.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 *
 * import org.agilewiki.jactor2.core.blades.BladeBase;
 * import org.agilewiki.jactor2.core.threading.BasicPlant;
 * import org.agilewiki.jactor2.core.processing.Reactor;
 * import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
 *
 * public class RequestSample {
 *
 *     public static void main(String[] args) throws Exception {
 *
 *         //A facility with two threads.
 *         final BasicPlant facility = new BasicPlant(2);
 *
 *         try {
 *
 *             //Create bladeA.
 *             SampleBlade2 bladeA = new SampleBlade2(new NonBlockingReactor(plant));
 *
 *             //Initialize bladeA to 1.
 *             bladeA.updateAReq(1).signal();
 *
 *             //Change bladeA to 2.
 *             System.out.println("was " + bladeA.updateAReq(2).call() + " but is now 2");
 *
 *             //Create bladeB with a reference to bladeA.
 *             IndirectBlade bladeB = new IndirectBlade(bladeA, new NonBlockingReactor(plant));
 *
 *             //Indirectly change bladeA to 42.
 *             System.out.println("was " + bladeB.indirectAReq(42).call() + " but is now 42");
 *
 *         } finally {
 *             //shutdown the facility
 *             plant.close();
 *         }
 *
 *     }
 *
 * }
 *
 * //A simple blades with state.
 * class SampleBlade2 extends BladeBase {
 *
 *     //Initial state is 0.
 *     private int state = 0;
 *
 *     //Create a SimpleBlade2.
 *     SampleBlade2(final Reactor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Return an update request.
 *     AsyncRequest&lt;Integer&gt; updateAReq(final int _newState) {
 *         return new AsyncBladeRequest&lt;Integer&gt;() {
 *
 *             {@literal @}Override
 *             protected void processAsyncRequest() throws Exception {
 *                 int oldState = state;
 *                 state = _newState; //assign the new state
 *                 processAsyncResponse(oldState); //return the old state.
 *             }
 *         };
 *     }
 *
 * }
 *
 * //A blades which operates on another blades.
 * class IndirectBlade extends BladeBase {
 *
 *     //The other blades.
 *     private final SampleBlade2 bladeA;
 *
 *     //Create an IndirectBlade with a reference to another blades.
 *     IndirectBlade(final SampleBlade2 _bladeA, final Reactor _messageProcessor) throws Exception {
 *         bladeA = _bladeA;
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Return a request to update the other blades and return its new state.
 *     AsyncRequest&lt;Integer&gt; indirectAReq(final int _newState) {
 *         return new AsyncBladeRequest&lt;Integer&gt;() {
 *             AsyncRequest&lt;Integer&gt; dis = this;
 *
 *             {@literal @}Override
 *             protected void processAsyncRequest() throws Exception {
 *
 *                 //Get a request from the other blades.
 *                 AsyncRequest&lt;Integer&gt; req = bladeA.updateAReq(_newState);
 *
 *                 //Send the request to the other blades.
 *                 send(req, new AsyncResponseProcessor&lt;Integer&gt;() {
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
 * @param <RESPONSE_TYPE> The class of the result returned after the AsyncRequest operates on the target blades.
 */
public abstract class AsyncRequest<RESPONSE_TYPE> implements Request<RESPONSE_TYPE>,
        AsyncResponseProcessor<RESPONSE_TYPE> {

    private final AsyncRequestImpl<RESPONSE_TYPE> asyncRequestImpl;

    /**
     * Create an AsyncRequest and bind it to its target targetReactor.
     *
     * @param _targetReactor The targetReactor where this AsyncRequest Objects is passed for processing.
     *                       The thread owned by this targetReactor will process this AsyncRequest.
     */
    public AsyncRequest(final Reactor _targetReactor) {
        asyncRequestImpl = new AsyncRequestImpl<RESPONSE_TYPE>(this, _targetReactor);
    }

    /**
     * The processAsyncRequest method will be invoked by the target Reactor on its own thread
     * when the AsyncRequest is dequeued from the target inbox for processing.
     */
    abstract public void processAsyncRequest() throws Exception;

    @Override
    public AsyncRequestImpl<RESPONSE_TYPE> asRequestImpl() {
        return asyncRequestImpl;
    }

    @Override
    public Reactor getTargetReactor() {
        return asyncRequestImpl.getTargetReactor();
    }

    protected void setNoHungRequestCheck() {
        asyncRequestImpl.setNoHungRequestCheck();
    }

    public int getPendingResponseCount() {
        return asyncRequestImpl.getPendingResponseCount();
    }

    @Override
    public void processAsyncResponse(final RESPONSE_TYPE _response)
            throws Exception {
        asyncRequestImpl.processAsyncResponse(_response);
    }

    /**
     * Returns an exception as a response instead of throwing it.
     * But regardless of how a response is returned, if the response is an exception it
     * is passed to the exception handler of the blades that did the call or doSend on the request.
     *
     * @param _response An exception.
     */
    public void processAsyncException(final Exception _response)
            throws Exception {
        asyncRequestImpl.processAsyncException(_response);
    }

    public <RT> void send(final Request<RT> _request,
                             final AsyncResponseProcessor<RT> _responseProcessor)
            throws Exception {
        asyncRequestImpl.send(_request, _responseProcessor);
    }

    public <RT, RT2> void send(final Request<RT> _request,
                                  final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse)
            throws Exception {
        asyncRequestImpl.send(_request, _dis, _fixedResponse);
    }

    @Override
    public void signal() throws Exception {
        asyncRequestImpl.signal();
    }

    @Override
    public RESPONSE_TYPE call() throws Exception {
        return asyncRequestImpl.call();
    }

    @Override
    public <RT> RT local(final SyncRequest<RT> _syncRequest)
            throws Exception {
        return asyncRequestImpl.local(_syncRequest);
    }

    /**
     * Replace the current ExceptionHandler with another.
     * <p>
     * When an event or request message is processed by a targetReactor, the current
     * exception handler is set to null. When a request is sent by a targetReactor, the
     * current exception handler is saved in the outgoing message and restored when
     * the response message is processed.
     * </p>
     *
     * @param _exceptionHandler The exception handler to be used now.
     *                          May be null if the default exception handler is to be used.
     * @return The exception handler that was previously in effect, or null if the
     * default exception handler was in effect.
     */
    public ExceptionHandler<RESPONSE_TYPE> setExceptionHandler(
            final ExceptionHandler<RESPONSE_TYPE> _exceptionHandler) {
        return asyncRequestImpl.setExceptionHandler(_exceptionHandler);
    }
}
