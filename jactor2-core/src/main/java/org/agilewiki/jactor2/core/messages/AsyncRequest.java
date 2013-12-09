package org.agilewiki.jactor2.core.messages;

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
 * import org.agilewiki.jactor2.core.threading.Plant;
 * import org.agilewiki.jactor2.core.processing.Reactor;
 * import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
 *
 * public class RequestSample {
 *
 *     public static void main(String[] args) throws Exception {
 *
 *         //A facility with two threads.
 *         final Plant facility = new Plant(2);
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
public abstract class AsyncRequest<RESPONSE_TYPE> extends
        RequestBase<RESPONSE_TYPE> implements
        AsyncResponseProcessor<RESPONSE_TYPE> {

    /**
     * Create an AsyncRequest and bind it to its target targetReactor.
     *
     * @param _targetReactor The targetReactor where this AsyncRequest Objects is passed for processing.
     *                       The thread owned by this targetReactor will process this AsyncRequest.
     */
    public AsyncRequest(final Reactor _targetReactor) {
        super(_targetReactor);
    }

    /**
     * The processAsyncRequest method will be invoked by the target Reactor on its own thread
     * when the AsyncRequest is dequeued from the target inbox for processing.
     */
    abstract protected void processAsyncRequest() throws Exception;

    @Override
    public void processAsyncResponse(final RESPONSE_TYPE _response)
            throws Exception {
        processObjectResponse(_response);
    }

    /**
     * The processAsyncResponse method accepts the response of a request.
     * <p>
     * This method need not be thread-safe, as it
     * is always invoked from the same light-weight thread (targetReactor) that passed the
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
     * is passed to the exception handler of the blades that did the call or doSend on the request.
     *
     * @param _response An exception.
     */
    public void processAsyncException(final Exception _response)
            throws Exception {
        processObjectResponse(_response);
    }

    @Override
    protected void processRequestMessage() throws Exception {
        processAsyncRequest();
    }

    protected <RT> void send(final RequestBase<RT> _request,
            final AsyncResponseProcessor<RT> _responseProcessor)
            throws Exception {
        RequestBase.doSend(targetReactor, _request, _responseProcessor);
    }

    protected <RT, RT2> void send(final RequestBase<RT> _request,
            final AsyncResponseProcessor<RT2> _dis, final RT2 _fixedResponse)
            throws Exception {
        RequestBase.doSend(targetReactor, _request,
                new AsyncResponseProcessor<RT>() {
                    @Override
                    public void processAsyncResponse(final RT _response)
                            throws Exception {
                        _dis.processAsyncResponse(_fixedResponse);
                    }
                });
    }
}
