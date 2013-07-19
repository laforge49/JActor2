package org.agilewiki.jactor2.api;

/**
 * <p>
 * Message encapsulates the user/application Request which are queued in the Actor's mailbox. The lightweight
 * thread associated with the Actor's mailbox will process the Message asynchronously. Considering the
 * scenario where multiple Actors are required to do the processing e.g PActor1 ---> PActor2 ---> PActor3.
 * The initial Request is passed to the PActor1 which might further pass the ResponseProcessor to PActor2
 * and PActor2 further to PActor3. The send method of the MailBox is used to pass ResponseProcessor across
 * PActor chain as considered in the scenario.
 * </p>
 */

public class Message implements AutoCloseable {
    private final boolean foreign;
    private final MessageSource messageSource;
    private final Actor targetActor;
    private final Message oldMessage;
    private final _Request<?, Actor> request;
    private final ExceptionHandler sourceExceptionHandler;
    private final ResponseProcessor<?> responseProcessor;
    private boolean responsePending = true;
    private Object response;

    public <E, A extends Actor> Message(final boolean _foreign,
                                        final MessageSource _source,
                                        final A _targetActor,
                                        final Message _old,
                                        final _Request<E, A> _request,
                                        final ExceptionHandler _handler,
                                        final ResponseProcessor _rp) {
        messageSource = _source;
        foreign = _foreign;
        targetActor = _targetActor;
        oldMessage = _old;
        request = (_Request<?, Actor>) _request;
        sourceExceptionHandler = _handler;
        responseProcessor = _rp;
    }

    /**
     * Returns true when the response is to be sent to another mailbox factory.
     *
     * @return True when the response is to be sent to another mailbox factory.
     */
    public boolean isForeign() {
        return foreign;
    }

    /**
     * @return the responsePending
     */
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
     * @return the messageSource
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * @return the messageSource
     */
    public Actor getTargetActor() {
        return targetActor;
    }

    /**
     * @return the oldMessage
     */
    public Message getOldMessage() {
        return oldMessage;
    }

    /**
     * @return the request
     */
    public _Request<?, Actor> getRequest() {
        return request;
    }

    /**
     * @return the sourceExceptionHandler
     */
    public ExceptionHandler getSourceExceptionHandler() {
        return sourceExceptionHandler;
    }

    /**
     * @return the responseProcessor
     */
    public ResponseProcessor<?> getResponseProcessor() {
        return responseProcessor;
    }

    @Override
    public void close() throws Exception {
        if (!responsePending)
            return;
        responsePending = false;
        response = new ServiceClosedException();
        messageSource.incomingResponse(this, null);
    }

    public final void event()
            throws Exception {
        targetActor.getMailbox().unbufferedAddMessages(this, false);
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

    public void eval(final Mailbox _targetMailbox) {
        if (responsePending) {
            processRequestMessage(_targetMailbox);
        } else {
            processResponseMessage(_targetMailbox);
        }
    }

    private void processRequestMessage(final Mailbox _targetMailbox) {
        System.out.println("processRequestMessage");
        final MailboxFactory mailboxFactory = _targetMailbox.getMailboxFactory();
        if (isForeign())
            mailboxFactory.addAutoClosable(this);
        _targetMailbox.setExceptionHandler(null);
        _targetMailbox.setCurrentMessage(this);
        final _Request<?, Actor> request = getRequest();
        try {
            request.processRequest(
                    getTargetActor(),
                    new Transport() {
                        @Override
                        public void processResponse(final Object response)
                                throws Exception {
                            if (isForeign())
                                mailboxFactory.removeAutoClosable(Message.this);
                            if (!isResponsePending())
                                return;
                            if (getResponseProcessor() != EventResponseProcessor.SINGLETON) {
                                setResponse(response);
                                getMessageSource().incomingResponse(Message.this, _targetMailbox);
                            } else {
                                if (response instanceof Throwable) {
                                    _targetMailbox.getLogger().warn("Uncaught throwable",
                                            (Throwable) response);
                                }
                            }
                        }

                        @Override
                        public MailboxFactory getMailboxFactory() {
                            MessageSource ms = getMessageSource();
                            if (ms == null)
                                return null;
                            if (!(ms instanceof Mailbox))
                                return null;
                            return ((Mailbox) ms).getMailboxFactory();
                        }

                        @Override
                        public void processException(Exception response) throws Exception {
                            processResponse((Object) response);
                        }
                    });
        } catch (final Throwable t) {
            if (isForeign())
                mailboxFactory.removeAutoClosable(this);
            processThrowable(_targetMailbox, t);
        }
    }

    private void processResponseMessage(final Mailbox _targetMailbox) {
        System.out.println("processResponseMessage");
        final Object response = this.getResponse();
        _targetMailbox.setExceptionHandler(getSourceExceptionHandler());
        _targetMailbox.setCurrentMessage(getOldMessage());
        if (response instanceof Throwable) {
            System.out.println("response is a Throwable");
            processThrowable(_targetMailbox, (Throwable) response);
            return;
        }
        @SuppressWarnings("rawtypes")
        final ResponseProcessor responseProcessor = this
                .getResponseProcessor();
        try {
            responseProcessor.processResponse(response);
        } catch (final Throwable t) {
            processThrowable(_targetMailbox, t);
        }
    }

    private void processThrowable(final Mailbox _targetMailbox, final Throwable _t) {
        System.out.println("processThrowable");
        final _Request<?, Actor> req = request;
        ExceptionHandler exceptionHandler = _targetMailbox.getExceptionHandler();
        if (exceptionHandler != null) {
            System.out.println("has exception handler");
            try {
                exceptionHandler.processException(_t);
            } catch (final Throwable u) {
                _targetMailbox.getLogger().error("Exception handler unable to process throwable "
                        + exceptionHandler.getClass().getName(), u);
                if (!(responseProcessor instanceof EventResponseProcessor)) {
                    if (!responsePending)
                        return;
                    setResponse(u);
                    messageSource.incomingResponse(this, _targetMailbox);
                } else {
                    _targetMailbox.getLogger().error("Thrown by exception handler and uncaught "
                            + exceptionHandler.getClass().getName(), _t);
                }
            }
        } else {
            System.out.println("no exception handler");
            if (!responsePending) {
                System.out.println("no response pending");
                return;
            }
            setResponse(_t);
            if (!(responseProcessor instanceof EventResponseProcessor))
                messageSource.incomingResponse(this, _targetMailbox);
            else {
                _targetMailbox.getLogger().warn("Uncaught throwable", _t);
            }
        }
    }

}
