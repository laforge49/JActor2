package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessorBase;

import java.util.concurrent.Semaphore;

/**
 * Request instances are used for passing both 1-way and 2-way messages between actors.
 * Requests are typically created as an anonymous class within the targeted Actor and are bound
 * to that actor's message processor.
 * The signal (1-way messaging) and call method (2-way messaging) pass unbuffered messages
 * to the target actor immediately, where they are enqueued for processing. But rather than being
 * sent immediately, request messages passed using the send method
 * (2-way messages) and all response messages are buffered for improved throughput. The send method
 * also supports thread migration.
 * <p/>
 * Some care needs to be taken with the parameters passed to the target actor when creating a
 * Request. The application must take care not to change the contents of these parameters,
 * as their will likely be accessed from a different thread when the target actor
 * is operated on.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 *
 * import org.agilewiki.jactor2.core.ActorBase;
 * import org.agilewiki.jactor2.core.context.JAContext;
 * import org.agilewiki.jactor2.core.processing.MessageProcessor;
 * import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
 *
 * public class RequestSample {
 *
 *     public static void main(String[] args) throws Exception {
 *
 *         //A context with two threads.
 *         final JAContext jaContext = new JAContext(2);
 *
 *         try {
 *
 *             //Create actorA.
 *             SampleActor2 actorA = new SampleActor2(new NonBlockingMessageProcessor(jaContext));
 *
 *             //Initialize actorA to 1.
 *             actorA.updateReq(1).signal();
 *
 *             //Change actorA to 2.
 *             System.out.println("was " + actorA.updateReq(2).call() + " but is now 2");
 *
 *             //Create actorB with a reference to actorA.
 *             IndirectActor actorB = new IndirectActor(actorA, new NonBlockingMessageProcessor(jaContext));
 *
 *             //Indirectly change actorA to 42.
 *             System.out.println("was " + actorB.indirectReq(42).call() + " but is now 42");
 *
 *         } finally {
 *             //shutdown the context
 *             jaContext.close();
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
 *     Request&lt;Integer&gt; updateReq(final int _newState) {
 *         return new Request&lt;Integer&gt;(getMessageProcessor()) {
 *
 *             {@literal @}Override
 *             public void processRequest(Transport&lt;Integer&gt; _transport) throws Exception {
 *                 int oldState = state;
 *                 state = _newState; //assign the new state
 *                 _transport.processResponse(oldState); //return the old state.
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
 *     Request&lt;Integer&gt; indirectReq(final int _newState) {
 *         return new Request&lt;Integer&gt;(getMessageProcessor()) {
 *
 *             {@literal @}Override
 *             public void processRequest(final Transport&lt;Integer&gt; _transport) throws Exception {
 *
 *                 //Get a request from the other actor.
 *                 Request&lt;Integer&gt; req = actorA.updateReq(_newState);
 *
 *                 //Send the request to the other actor.
 *                 req.send(getMessageProcessor(), new ResponseProcessor&lt;Integer&gt;() {
 *
 *                     {@literal @}Override
 *                     public void processResponse(Integer response) throws Exception {
 *
 *                         //Return the old state.
 *                         _transport.processResponse(response);
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
 * @param <RESPONSE_TYPE> The class of the result returned after the Request operates on the target actor.
 */
public abstract class Request<RESPONSE_TYPE> {

    /**
     * The message processor where this Request Objects is passed for processing. The thread
     * owned by this message processor will process the Request.
     */
    private final MessageProcessorBase messageProcessor;

    /**
     * Create an Request and bind it to its target message processor.
     *
     * @param _targetMessageProcessor The message processor where this Request Objects is passed for processing.
     *                                The thread owned by this message processor will process this Request.
     */
    public Request(final MessageProcessor _targetMessageProcessor) {
        if (_targetMessageProcessor == null) {
            throw new NullPointerException("targetMessageProcessor");
        }
        this.messageProcessor = (MessageProcessorBase) _targetMessageProcessor;
    }

    /**
     * Returns the MessageProcessor to which this Request is bound and to which this Request is to be passed.
     *
     * @return The target MessageProcessor.
     */
    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    /**
     * Passes this Request to the target MessageProcessor without any result being passed back.
     * I.E. The signal method results in a 1-way message being passed.
     * If an exception is thrown while processing this Request,
     * that exception is simply logged as a warning.
     */
    public void signal() throws Exception {
        final RequestMessage message = new RequestMessage(false, null, null,
                this, null, SignalResponseProcessor.SINGLETON);
        message.signal(messageProcessor);
    }

    /**
     * Passes this Request together with the ResponseProcessor to the target MessageProcessor.
     * Responses are passed back via the message processor of the source actor and processed by the
     * provided ResponseProcessor and any exceptions
     * raised while processing the request are processed by the exception handler active when
     * the send method was called.
     *
     * @param _source The message processor on whose thread this method was invoked and which
     *                will buffer this Request and subsequently receive the result for
     *                processing on the same thread.
     * @param _rp     Passed with this request and then returned with the result, the
     *                ResponseProcessor is used to process the result on the same thread
     *                that originally invoked this method. If null, then no response is returned.
     */
    public void send(final MessageProcessor _source,
                     final ResponseProcessor<RESPONSE_TYPE> _rp) throws Exception {
        MessageProcessorBase source = (MessageProcessorBase) _source;
        if (!source.isRunning())
            throw new IllegalStateException(
                    "A valid source message processor can not be idle");
        ResponseProcessor<RESPONSE_TYPE> rp = _rp;
        if (rp == null)
            rp = (ResponseProcessor<RESPONSE_TYPE>) SignalResponseProcessor.SINGLETON;
        final RequestMessage message = new RequestMessage(
                source.getJAContext() != messageProcessor.getJAContext(),
                source,
                source.getCurrentMessage(),
                this,
                source.getExceptionHandler(),
                rp);
        message.send(messageProcessor);
    }

    /**
     * Passes this Request to the target MessageProcessor and blocks the current thread until
     * a result is returned. The call method sends the message directly without buffering,
     * as there is no message processor. The response message is buffered, though thread migration is
     * not possible.
     *
     * @return The result from applying this Request to the target actor.
     * @throws Exception If the result is an exception, it is thrown rather than being returned.
     */
    public RESPONSE_TYPE call() throws Exception {
        final Pender pender = new Pender();
        final RequestMessage<RESPONSE_TYPE> message = new RequestMessage<RESPONSE_TYPE>(true, pender, null,
                this, null, CallResponseProcessor.SINGLETON);
        return (RESPONSE_TYPE) message.call(messageProcessor);
    }

    /**
     * The processRequest method will be invoked by the target MessageProcessor on its own thread
     * when the Request is dequeued from the target inbox for processing.
     *
     * @param _transport The Transport that is responsible for passing the result back
     *                   to the originator of this Request.
     */
    abstract public void processRequest(final Transport<RESPONSE_TYPE> _transport)
            throws Exception;

    /**
     * Pender is used by the Request.call method to block the current thread until a
     * result is available and then either return the result or rethrow it if the result
     * is an exception.
     */
    private static final class Pender implements MessageSource {

        /**
         * Used to signal the arrival of a response.
         */
        private final Semaphore done = new Semaphore(0);

        /**
         * The result from the incoming response. May be null or an Exception.
         */
        private transient Object result;

        /**
         * Returns the response, which may be null. But if the response
         * is an exception, then it is thrown.
         *
         * @return The response or null, but not an exception.
         */
        Object pend() throws Exception {
            done.acquire();
            if (result instanceof Exception)
                throw (Exception) result;
            if (result instanceof Error)
                throw (Error) result;
            return result;
        }

        @Override
        public void incomingResponse(final Message _message,
                                     final MessageProcessor _responseSource) {
            this.result = ((RequestMessage) _message).getResponse();
            done.release();
        }
    }

    /**
     * An instance of ResponseProcessor that is used as a place holder when the Request.call
     * method is used.
     */
    final private static class CallResponseProcessor implements ResponseProcessor<Object> {
        /**
         * The singleton.
         */
        public static final CallResponseProcessor SINGLETON = new CallResponseProcessor();

        /**
         * Restrict the use of this class to being a singleton.
         */
        private CallResponseProcessor() {
        }

        @Override
        public void processResponse(final Object response) {
        }
    }

    /**
     * The Message subclass used to pass requests and to return the results.
     *
     * @param <RESPONSE_TYPE> The class of the result returned after the Request operates on the target actor.
     */
    private static final class RequestMessage<RESPONSE_TYPE> implements Message {

        /**
         * True when the result is to be returned via a message processor with a context
         * that differs from the context of the target message processor.
         */
        protected final boolean foreign;

        /**
         * The source message processor or pender that will receive the results.
         */
        protected final MessageSource messageSource;

        /**
         * The message targeted to the source message processor which, when processed,
         * resulted in this message.
         */
        protected final Message oldMessage;

        /**
         * The Request object carried by this message.
         */
        protected final Request<RESPONSE_TYPE> request;

        /**
         * The exception handler that was active in the source message processor at the time
         * when this message was created.
         */
        protected final ExceptionHandler sourceExceptionHandler;

        /**
         * The application object that will process the results.
         */
        protected final ResponseProcessor<?> responseProcessor;

        /**
         * True when a response to this message has not yet been determined.
         */
        protected boolean responsePending = true;

        /**
         * The response created when this message is applied to the target actor.
         */
        protected Object response;

        /**
         * Creates a request message.
         *
         * @param _foreign True when the result is to be returned via a message processor with a context
         *                 that differs from the context of the target message processor.
         * @param _source  The source message processor or pender that will receive the results.
         * @param _old     The message targeted to the source message processor which, when processed,
         *                 resulted in this message.
         * @param _request The Request object carried by this message.
         * @param _handler The exception handler that was active in the source message processor at the time
         *                 when this message was created.
         * @param _rp      The application object that will process the results.
         */
        RequestMessage(final boolean _foreign,
                       final MessageSource _source,
                       final Message _old,
                       final Request<RESPONSE_TYPE> _request,
                       final ExceptionHandler _handler,
                       final ResponseProcessor _rp) {
            messageSource = _source;
            foreign = _foreign;
            oldMessage = _old;
            request = _request;
            sourceExceptionHandler = _handler;
            responseProcessor = _rp;
        }

        @Override
        public boolean isForeign() {
            return foreign;
        }

        @Override
        public boolean isResponsePending() {
            return responsePending;
        }

        /**
         * @param _response the response being returned
         */
        void setResponse(final Object _response, final MessageProcessor _activeMessageProcessor) {
            ((MessageProcessorBase) _activeMessageProcessor).requestEnd();
            responsePending = false;
            response = _response;
        }

        /**
         * Returns the response.
         *
         * @return The response.
         */
        Object getResponse() {
            return response;
        }

        /**
         * The response processor.
         *
         * @return The responseProcessor.
         */
        ResponseProcessor<?> getResponseProcessor() {
            return responseProcessor;
        }

        /**
         * Pass a 1-way message.
         *
         * @param _targetMessageProcessor The message processor that will receive the message.
         */
        final void signal(final MessageProcessor _targetMessageProcessor) throws Exception {
            ((MessageProcessorBase) _targetMessageProcessor).unbufferedAddMessage(this, false);
        }

        /**
         * A 2-way message exchange between the message processors.
         *
         * @param _targetMessageProcessor The message processor that will receive the message.
         */
        final void send(final MessageProcessor _targetMessageProcessor) throws Exception {
            MessageProcessorBase sourceMessageProcessor = (MessageProcessorBase) messageSource;
            boolean local = sourceMessageProcessor == _targetMessageProcessor;
            if (local || !sourceMessageProcessor.buffer(this, _targetMessageProcessor))
                ((MessageProcessorBase) _targetMessageProcessor).unbufferedAddMessage(this, local);
        }

        /**
         * A 2-way message exchange between a Pender and the target message processor.
         *
         * @param _targetMessageProcessor The message processor that will receive the message.
         */
        final Object call(final MessageProcessor _targetMessageProcessor) throws Exception {
            ((MessageProcessorBase) _targetMessageProcessor).unbufferedAddMessage(this, false);
            return ((Pender) messageSource).pend();
        }

        @Override
        public void close() {
            if (!responsePending)
                return;
            responsePending = false;
            response = new ServiceClosedException();
            messageSource.incomingResponse(this, null);
        }

        /**
         * Process a request or the response.
         *
         * @param _activeMessageProcessor The message processor whose thread is to evaluate the request/response.
         */
        public void eval(final MessageProcessor _activeMessageProcessor) {
            if (responsePending) {
                processRequestMessage(_activeMessageProcessor);
            } else {
                processResponseMessage(_activeMessageProcessor);
            }
        }

        /**
         * Process a request.
         *
         * @param _targetMessageProcessor The message processor whose thread is to evaluate the request.
         */
        private void processRequestMessage(final MessageProcessor _targetMessageProcessor) {
            final MessageProcessorBase targetMessageProcessor = (MessageProcessorBase) _targetMessageProcessor;
            final JAContext jaContext = targetMessageProcessor.getJAContext();
            if (foreign)
                jaContext.addAutoClosable(this);
            targetMessageProcessor.setExceptionHandler(null);
            targetMessageProcessor.setCurrentMessage(this);
            targetMessageProcessor.requestBegin();
            try {
                request.processRequest(
                        new Transport() {
                            @Override
                            public void processResponse(final Object response)
                                    throws Exception {
                                if (foreign)
                                    jaContext.removeAutoClosable(RequestMessage.this);
                                if (!responsePending)
                                    return;
                                setResponse(response, targetMessageProcessor);
                                if (getResponseProcessor() != SignalResponseProcessor.SINGLETON) {
                                    messageSource.incomingResponse(RequestMessage.this, targetMessageProcessor);
                                } else {
                                    if (response instanceof Throwable) {
                                        targetMessageProcessor.getLogger().warn("Uncaught throwable",
                                                (Throwable) response);
                                    }
                                }
                            }

                            @Override
                            public JAContext getJAContext() {
                                if (messageSource == null)
                                    return null;
                                if (!(messageSource instanceof MessageProcessor))
                                    return null;
                                return ((MessageProcessorBase) messageSource).getJAContext();
                            }

                            @Override
                            public void processException(Exception response) throws Exception {
                                processResponse((Object) response);
                            }
                        });
            } catch (final Throwable t) {
                if (foreign)
                    jaContext.removeAutoClosable(this);
                processThrowable(targetMessageProcessor, t);
            }
        }

        /**
         * Process a response.
         *
         * @param _sourceMessageProcessor The message processor whose thread is to evaluate the response.
         */
        private void processResponseMessage(final MessageProcessor _sourceMessageProcessor) {
            MessageProcessorBase sourceMessageProcessor = (MessageProcessorBase) _sourceMessageProcessor;
            sourceMessageProcessor.setExceptionHandler(sourceExceptionHandler);
            sourceMessageProcessor.setCurrentMessage(oldMessage);
            if (response instanceof Throwable) {
                oldMessage.processThrowable(sourceMessageProcessor, (Throwable) response);
                return;
            }
            @SuppressWarnings("rawtypes")
            final ResponseProcessor responseProcessor = this
                    .getResponseProcessor();
            try {
                responseProcessor.processResponse(response);
            } catch (final Throwable t) {
                oldMessage.processThrowable(sourceMessageProcessor, t);
            }
        }

        @Override
        public void processThrowable(final MessageProcessor _activeMessageProcessor, final Throwable _t) {
            MessageProcessorBase activeMessageProcessor = (MessageProcessorBase) _activeMessageProcessor;
            ExceptionHandler exceptionHandler = activeMessageProcessor.getExceptionHandler();
            if (exceptionHandler != null) {
                try {
                    exceptionHandler.processException(_t);
                } catch (final Throwable u) {
                    activeMessageProcessor.getLogger().error("Exception handler unable to process throwable "
                            + exceptionHandler.getClass().getName(), u);
                    if (!(responseProcessor instanceof SignalResponseProcessor)) {
                        if (!responsePending)
                            return;
                        setResponse(u, activeMessageProcessor);
                        messageSource.incomingResponse(this, activeMessageProcessor);
                    } else {
                        activeMessageProcessor.getLogger().error("Thrown by exception handler and uncaught "
                                + exceptionHandler.getClass().getName(), _t);
                    }
                }
            } else {
                if (!responsePending) {
                    return;
                }
                setResponse(_t, activeMessageProcessor);
                if (!(responseProcessor instanceof SignalResponseProcessor))
                    messageSource.incomingResponse(this, activeMessageProcessor);
                else {
                    activeMessageProcessor.getLogger().warn("Uncaught throwable", _t);
                }
            }
        }
    }
}
