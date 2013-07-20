package org.agilewiki.jactor2.api;

public class RequestMessage extends Message {
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

    protected void processThrowable(final Mailbox _activeMailbox, final Throwable _t) {
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
