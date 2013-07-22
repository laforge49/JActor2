package org.agilewiki.jactor2.api;

import java.util.concurrent.Semaphore;

/**
 * Request objects are typically created as an anonymous class within the targeted Actor and bound
 * to that actor's mailbox. By this means the request can update an actor's state in a thread-safe way.
 * <p/>
 * <pre>
 *     public class ActorA {
 *         private final Mailbox mailbox;
 *         public final Request&lt;String&gt; hi1;
 *
 *         public Actor1(final Mailbox _mailbox) {
 *             mailbox = _mailbox;
 *
 *             hi1 = new Request&lt;String&gt;(mailbox) {
 *                 public void processRequest(final ResponseProcessor&lt;String&gt; _rp)
 *                         throws Exception {
 *                     responseProcessor.processResponse("Hello world!");
 *                }
 *             };
 *         }
 *     }
 * </pre>
 *
 * @param <RESPONSE_TYPE> The class of the result returned when this Request is processed.
 */
public abstract class Request<RESPONSE_TYPE> {

    /**
     * The mailbox where this Request Objects is passed for processing. The thread
     * owned by this mailbox will process this Request.
     */
    private final Mailbox mailbox;

    /**
     * Create an Request and bind it to its target mailbox.
     *
     * @param _targetMailbox The mailbox where this Request Objects is passed for processing.
     *                       The thread owned by this mailbox will process this Request.
     */
    public Request(final Mailbox _targetMailbox) {
        if (_targetMailbox == null) {
            throw new NullPointerException("targetMailbox");
        }
        this.mailbox = _targetMailbox;
    }

    /**
     * Returns the Mailbox to which this Request is bound and to which this Request is to be passed.
     *
     * @return The target Mailbox.
     */
    public Mailbox getMailbox() {
        return mailbox;
    }

    /**
     * Passes this Request to the target Mailbox without a return address.
     * No result is passed back and if an exception is thrown while processing this Request,
     * that exception is simply logged as a warning.
     *
     * @param _source The mailbox on whose thread this method was invoked and which
     *                will buffer this Request.
     */
    public void signal(final Mailbox _source) throws Exception {
        if (!_source.isRunning())
            throw new IllegalStateException(
                    "A valid source mailbox can not be idle");
        final RequestMessage message = new RequestMessage(false, mailbox, null,
                this, null, EventResponseProcessor.SINGLETON);
        message.signal(mailbox);
    }

    /**
     * Passes this Request together with the ResponseProcessor to the target Mailbox.
     *
     * @param _source The mailbox on whose thread this method was invoked and which
     *                will buffer this Request and subsequently receive the result for
     *                processing on the same thread.
     * @param _rp     Passed with this request and then returned with the result, the
     *                ResponseProcessor is used to process the result on the same thread
     *                that originally invoked this method.
     */
    public void send(final Mailbox _source,
                     final ResponseProcessor<RESPONSE_TYPE> _rp) throws Exception {
        if (!_source.isRunning())
            throw new IllegalStateException(
                    "A valid source mailbox can not be idle");
        final RequestMessage message = new RequestMessage(
                _source.getMailboxFactory() != mailbox.getMailboxFactory(),
                _source,
                _source.getCurrentMessage(),
                this,
                _source.getExceptionHandler(),
                _rp);
        message.send(mailbox);
    }

    /**
     * Passes this Request to the target Mailbox and blocks the current thread until
     * a result is returned.
     *
     * @return The result from processing this Request.
     * @throws Exception If the result is an exception, it is thrown rather than being returned.
     */
    public RESPONSE_TYPE call() throws Exception {
        final Caller caller = new Caller();
        final RequestMessage message = new RequestMessage(true, caller, null,
                this, null, DummyResponseProcessor.SINGLETON);
        return (RESPONSE_TYPE) message.call(mailbox);
    }

    /**
     * The processRequest method will be invoked by the target Mailbox on its own thread
     * when this Request is received for processing.
     *
     * @param _transport The Transport that is responsible for passing the result back
     *                   to the originator of this Request. Either an Exception must be thrown or
     *                   the _rp.processResponse method must be invoked.
     */
    abstract public void processRequest(final Transport<RESPONSE_TYPE> _transport)
            throws Exception;
}

class Caller implements MessageSource {

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
    public Object call() throws Exception {
        done.acquire();
        if (result instanceof Exception)
            throw (Exception) result;
        if (result instanceof Error)
            throw (Error) result;
        return result;
    }

    @Override
    public void incomingResponse(final Message _message,
                                 final Mailbox _responseSource) {
        this.result = ((RequestMessage) _message).getResponse();
        done.release();
    }
}

class RequestMessage implements Message {
    protected final boolean foreign;
    protected final MessageSource messageSource;
    protected final Message oldMessage;
    protected final Request<?> request;
    protected final ExceptionHandler sourceExceptionHandler;
    protected final ResponseProcessor<?> responseProcessor;
    protected boolean responsePending = true;
    protected Object response;

    public <E, A extends Actor> RequestMessage(final boolean _foreign,
                                               final MessageSource _source,
                                               final Message _old,
                                               final Request<?> _request,
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
    public void setResponse(final Object _response) {
        responsePending = false;
        response = _response;
    }

    /**
     * @return the response
     */
    public Object getResponse() {
        return response;
    }

    /**
     * @return the responseProcessor
     */
    public ResponseProcessor<?> getResponseProcessor() {
        return responseProcessor;
    }

    public final void signal(final Mailbox _targetMailbox) throws Exception {
        Mailbox sourceMailbox = (Mailbox) messageSource;
        boolean local = sourceMailbox == _targetMailbox;
        if (local || !sourceMailbox.buffer(this, _targetMailbox))
            _targetMailbox.unbufferedAddMessages(this, local);
    }

    public final void send(final Mailbox _targetMailbox) throws Exception {
        Mailbox sourceMailbox = (Mailbox) messageSource;
        boolean local = sourceMailbox == _targetMailbox;
        if (local || !sourceMailbox.buffer(this, _targetMailbox))
            _targetMailbox.unbufferedAddMessages(this, local);
    }

    public final Object call(final Mailbox _targetMailbox) throws Exception {
        _targetMailbox.unbufferedAddMessages(this, false);
        return ((Caller) messageSource).call();
    }

    @Override
    public void close() throws Exception {
        if (!responsePending)
            return;
        responsePending = false;
        response = new ServiceClosedException();
        messageSource.incomingResponse(this, null);
    }

    public void eval(final Mailbox _targetMailbox) {
        if (responsePending) {
            processRequestMessage(_targetMailbox);
        } else {
            processResponseMessage(_targetMailbox);
        }
    }

    private void processRequestMessage(final Mailbox _targetMailbox) {
        final MailboxFactory mailboxFactory = _targetMailbox.getMailboxFactory();
        if (foreign)
            mailboxFactory.addAutoClosable(this);
        _targetMailbox.setExceptionHandler(null);
        _targetMailbox.setCurrentMessage(this);
        try {
            request.processRequest(
                    new Transport() {
                        @Override
                        public void processResponse(final Object response)
                                throws Exception {
                            if (foreign)
                                mailboxFactory.removeAutoClosable(RequestMessage.this);
                            if (!responsePending)
                                return;
                            if (getResponseProcessor() != EventResponseProcessor.SINGLETON) {
                                setResponse(response);
                                messageSource.incomingResponse(RequestMessage.this, _targetMailbox);
                            } else {
                                if (response instanceof Throwable) {
                                    _targetMailbox.getLogger().warn("Uncaught throwable",
                                            (Throwable) response);
                                }
                            }
                        }

                        @Override
                        public MailboxFactory getMailboxFactory() {
                            if (messageSource == null)
                                return null;
                            if (!(messageSource instanceof Mailbox))
                                return null;
                            return ((Mailbox) messageSource).getMailboxFactory();
                        }

                        @Override
                        public void processException(Exception response) throws Exception {
                            processResponse((Object) response);
                        }
                    });
        } catch (final Throwable t) {
            if (foreign)
                mailboxFactory.removeAutoClosable(this);
            processThrowable(_targetMailbox, t);
        }
    }

    private void processResponseMessage(final Mailbox _sourceMailbox) {
        _sourceMailbox.setExceptionHandler(sourceExceptionHandler);
        _sourceMailbox.setCurrentMessage(oldMessage);
        if (response instanceof Throwable) {
            oldMessage.processThrowable(_sourceMailbox, (Throwable) response);
            return;
        }
        @SuppressWarnings("rawtypes")
        final ResponseProcessor responseProcessor = this
                .getResponseProcessor();
        try {
            responseProcessor.processResponse(response);
        } catch (final Throwable t) {
            oldMessage.processThrowable(_sourceMailbox, t);
        }
    }

    @Override
    public void processThrowable(final Mailbox _activeMailbox, final Throwable _t) {
        ExceptionHandler exceptionHandler = _activeMailbox.getExceptionHandler();
        if (exceptionHandler != null) {
            try {
                exceptionHandler.processException(_t);
            } catch (final Throwable u) {
                _activeMailbox.getLogger().error("Exception handler unable to process throwable "
                        + exceptionHandler.getClass().getName(), u);
                if (!(responseProcessor instanceof EventResponseProcessor)) {
                    if (!responsePending)
                        return;
                    setResponse(u);
                    messageSource.incomingResponse(this, _activeMailbox);
                } else {
                    _activeMailbox.getLogger().error("Thrown by exception handler and uncaught "
                            + exceptionHandler.getClass().getName(), _t);
                }
            }
        } else {
            if (!responsePending) {
                return;
            }
            setResponse(_t);
            if (!(responseProcessor instanceof EventResponseProcessor))
                messageSource.incomingResponse(this, _activeMailbox);
            else {
                _activeMailbox.getLogger().warn("Uncaught throwable", _t);
            }
        }
    }
}
