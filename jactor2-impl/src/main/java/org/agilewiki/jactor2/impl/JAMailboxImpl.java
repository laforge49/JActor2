package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.Map.Entry;

/**
 * Base class for mailboxes.
 */
abstract public class JAMailboxImpl implements JAMailbox {

    /**
     * Mailbox logger.
     */
    protected final Logger log;

    /**
     * The factory of this mailbox.
     */
    protected final JAMailboxFactory mailboxFactory;

    /**
     * The inbox, implemented as a local queue and a concurrent queue.
     */
    protected final Inbox inbox;

    /**
     * Initial size of the outbox for each unique message destination.
     */
    private final int initialBufferSize;

    /**
     * A table of outboxes, one for each unique message destination.
     */
    protected Map<JAMailbox, ArrayDeque<Message>> sendBuffer;

    /**
     * The currently active exception handler.
     */
    private ExceptionHandler exceptionHandler;

    /**
     * The request or signal message being processed.
     */
    private Message currentMessage;

    /**
     * Create a mailbox.
     *
     * @param _factory           The factory of this object.
     * @param _inbox             The inbox.
     * @param _log               The Mailbox log.
     * @param _initialBufferSize Initial size of the outbox for each unique message destination.
     */
    public JAMailboxImpl(final JAMailboxFactory _factory,
                         final Inbox _inbox,
                         final Logger _log,
                         final int _initialBufferSize) {
        mailboxFactory = _factory;
        inbox = _inbox;
        log = _log;
        initialBufferSize = _initialBufferSize;
        _factory.addAutoClosable(this);
    }

    @Override
    public final Message getCurrentMessage() {
        return currentMessage;
    }

    @Override
    public final boolean isEmpty() {
        return !inbox.isNonEmpty();
    }

    @Override
    public void close() throws Exception {
        if (sendBuffer == null)
            return;
        final Iterator<Entry<JAMailbox, ArrayDeque<Message>>> iter = sendBuffer
                .entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<JAMailbox, ArrayDeque<Message>> entry = iter.next();
            final JAMailbox target = entry.getKey();
            if (target.getMailboxFactory() != mailboxFactory) {
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                target.unbufferedAddMessages(messages);
            } else
                iter.remove();
        }
        while (true) {
            final Message message = inbox.poll();
            if (message == null)
                return;
            if (message.isForeign() && message.isResponsePending())
                try {
                    message.close();
                } catch (final Throwable t) {
                }
        }
    }

    @Override
    public final ExceptionHandler setExceptionHandler(
            final ExceptionHandler _handler) {
        if (!isRunning())
            throw new IllegalStateException(
                    "Attempt to set an exception handler on an idle mailbox");
        final ExceptionHandler rv = this.exceptionHandler;
        this.exceptionHandler = _handler;
        return rv;
    }

    @Override
    public final ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void unbufferedAddMessages(final Message _message, final boolean _local)
            throws Exception {
        if (mailboxFactory.isClosing()) {
            if (_message.isForeign() && _message.isResponsePending())
                try {
                    _message.close();
                } catch (final Throwable t) {
                }
            return;
        }
        inbox.offer(_local, _message);
        afterAdd();
    }

    @Override
    public void unbufferedAddMessages(final Queue<Message> _messages)
            throws Exception {
        if (mailboxFactory.isClosing()) {
            final Iterator<Message> itm = _messages.iterator();
            while (itm.hasNext()) {
                final Message message = itm.next();
                if (message.isForeign() && message.isResponsePending())
                    try {
                        message.close();
                    } catch (final Throwable t) {
                    }
            }
            return;
        }
        inbox.offer(_messages);
        afterAdd();
    }

    /**
     * Called after adding some message(s) to the inbox.
     */
    abstract protected void afterAdd() throws Exception;

    @Override
    public boolean buffer(final Message _message, final Mailbox _target) {
        if (mailboxFactory.isClosing())
            return false;
        ArrayDeque<Message> buffer;
        if (sendBuffer == null) {
            sendBuffer = new IdentityHashMap<JAMailbox, ArrayDeque<Message>>();
            buffer = null;
        } else {
            buffer = sendBuffer.get(_target);
        }
        if (buffer == null) {
            buffer = new ArrayDeque<Message>(initialBufferSize);
            sendBuffer.put((JAMailbox) _target, buffer);
        }
        buffer.add(_message);
        return true;
    }

    @Override
    public void run() {
        while (true) {
            final Message message = inbox.poll();
            if (message == null) {
                try {
                    onIdle();
                } catch (final MigrationException me) {
                    throw me;
                } catch (Exception e) {
                    log.error("Exception thrown by onIdle", e);
                }
                if (inbox.isNonEmpty())
                    continue;
                return;
            }
            if (message.isResponsePending())
                processRequestMessage(message);
            else
                processResponseMessage(message);
        }
    }

    /**
     * Called when all pending messages have been processed.
     */
    abstract protected void onIdle() throws Exception;

    /**
     * Process a request or signal message.
     *
     * @param _message The message to be processed.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void processRequestMessage(final Message _message) {
        if (_message.isForeign())
            mailboxFactory.addAutoClosable(_message);
        beforeProcessMessage(true, _message);
        try {
            exceptionHandler = null; //NOPMD
            currentMessage = _message;
            final _Request<?, Actor> request = _message.getRequest();
            try {
                request.processRequest(_message.getTargetActor(),
                        new Transport() {
                            @Override
                            public void processResponse(final Object response)
                                    throws Exception {
                                if (_message.isForeign())
                                    mailboxFactory.removeAutoClosable(_message);
                                if (!_message.isResponsePending())
                                    return;
                                if (_message.getResponseProcessor() != EventResponseProcessor.SINGLETON) {
                                    _message.setResponse(response);
                                    _message.getMessageSource()
                                            .incomingResponse(_message,
                                                    JAMailboxImpl.this);
                                } else {
                                    if (response instanceof Throwable) {
                                        log.warn("Uncaught throwable",
                                                (Throwable) response);
                                    }
                                }
                            }

                            @Override
                            public MailboxFactory getMailboxFactory() {
                                MessageSource ms = _message.getMessageSource();
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
                if (_message.isForeign())
                    mailboxFactory.removeAutoClosable(_message);
                processThrowable(t);
            }
        } finally {
            afterProcessMessage(true, _message);
        }
    }

    /**
     * Process a Throwable response.
     *
     * @param _t The Throwable response.
     */
    private void processThrowable(final Throwable _t) {
        if (!currentMessage.isResponsePending())
            return;
        final Message message = currentMessage;
        final _Request<?, Actor> req = message.getRequest();
        if (exceptionHandler != null) {
            try {
                exceptionHandler.processException(_t);
            } catch (final Throwable u) {
                log.error("Exception handler unable to process throwable "
                        + exceptionHandler.getClass().getName(), u);
                if (!(message.getResponseProcessor() instanceof EventResponseProcessor)) {
                    if (!message.isResponsePending())
                        return;
                    currentMessage.setResponse(u);
                    message.getMessageSource().incomingResponse(message,
                            JAMailboxImpl.this);
                } else {
                    log.error("Thrown by exception handler and uncaught "
                            + exceptionHandler.getClass().getName(), _t);
                }
            }
        } else {
            if (!message.isResponsePending())
                return;
            currentMessage.setResponse(_t);
            if (!(message.getResponseProcessor() instanceof EventResponseProcessor))
                message.getMessageSource().incomingResponse(message,
                        JAMailboxImpl.this);
            else {
                log.warn("Uncaught throwable", _t);
            }
        }
    }

    /**
     * Process a response message.
     *
     * @param _message A request message holding the response.
     */
    @SuppressWarnings("unchecked")
    private void processResponseMessage(final Message _message) {
        beforeProcessMessage(false, _message);
        try {
            final Object response = _message.getResponse();
            exceptionHandler = _message.getSourceExceptionHandler();
            currentMessage = _message.getOldMessage();
            if (response instanceof Throwable) {
                processThrowable((Throwable) response);
                return;
            }
            @SuppressWarnings("rawtypes")
            final ResponseProcessor responseProcessor = _message
                    .getResponseProcessor();
            try {
                responseProcessor.processResponse(response);
            } catch (final Throwable t) {
                processThrowable(t);
            }
        } finally {
            afterProcessMessage(false, _message);
        }
    }

    @Override
    public final void incomingResponse(final Message _message,
                                       final Mailbox _responseSource) {
        try {
            unbufferedAddMessages(_message, this == _responseSource ||
                    (_responseSource != null && this == _responseSource));
        } catch (final Throwable t) {
            log.error("unable to add response message", t);
        }
    }

    @Override
    public JAMailboxFactory getMailboxFactory() {
        return mailboxFactory;
    }

    /**
     * Called before running processXXXMessage(Message).
     *
     * @param _request True if the message does not contain a response.
     * @param _message The message about to be processed.
     */
    protected void beforeProcessMessage(final boolean _request,
                                        final Message _message) {
    }

    /**
     * Called after processing a message.
     *
     * @param _request True if the message did not previously contain a response.
     * @param _message The message that has been processed.
     */
    protected void afterProcessMessage(final boolean _request,
                                       final Message _message) {
    }
}
