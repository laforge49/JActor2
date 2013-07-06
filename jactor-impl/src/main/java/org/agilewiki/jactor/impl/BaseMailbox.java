package org.agilewiki.jactor.impl;

import org.agilewiki.jactor.api.*;
import org.slf4j.Logger;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

abstract public class BaseMailbox implements JAMailbox {

    private final Logger log;

    private final JAMailboxFactory mailboxFactory;
    private final MessageQueue inbox;
    private final AtomicReference<Thread> threadReference = new AtomicReference<Thread>();
    private final Runnable onIdle;
    private final int initialBufferSize;
    /**
     * For performance reasons, we want to differentiate between Mailboxes that
     * usually block their thread, and Mailboxes that usually don't block, and
     * return quickly.
     * <p/>
     * TODO: disable commandeering when true
     */
    private final boolean mayBlock;

    /**
     * Send buffer
     */
    protected Map<JAMailbox, ArrayDeque<Message>> sendBuffer;

    private ExceptionHandler exceptionHandler;
    private Message currentMessage;

    @Override
    public boolean isIdler() {
        return onIdle != null;
    }

    @Override
    public AtomicReference<Thread> getThreadReference() {
        return threadReference;
    }

    public BaseMailbox(final boolean _mayBlock, final Runnable _onIdle,
                       final JAMailboxFactory factory,
                       final MessageQueue messageQueue, final Logger _log,
                       final int _initialBufferSize) {
        mayBlock = _mayBlock;
        onIdle = _onIdle;
        mailboxFactory = factory;
        inbox = messageQueue;
        log = _log;
        initialBufferSize = _initialBufferSize;
        factory.addAutoClosable(this);
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
                target.addUnbufferedMessages(messages);
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
    public final <A extends Actor> void signal(final _Request<Void, A> request,
                                               final A targetActor) throws Exception {
        final Message message = inbox.createMessage(false, null, targetActor,
                null, request, null, EventResponseProcessor.SINGLETON);
        // No source mean never local and no buffering.
        addMessage(null, message, false);
    }

    /**
     * Same as signal(Request) until buffered message are implemented.
     */
    @Override
    public final <A extends Actor> void signal(final _Request<Void, A> request,
                                               final Mailbox source, final A targetActor) throws Exception {
        final MessageSource sourceMailbox = (MessageSource) source;
        if (!sourceMailbox.isRunning())
            throw new IllegalStateException(
                    "A valid source mailbox can not be idle");
        final Message message = sourceMailbox.createMessage(false, inbox,
                request, targetActor, EventResponseProcessor.SINGLETON);
        boolean local = false;
        if (source instanceof JAMailbox)
            local = this == source ||
                    (source != null && threadReference.get() == ((JAMailbox) source).getThreadReference().get());
        addMessage(sourceMailbox, message, local);
    }

    @Override
    public final <E, A extends Actor> void send(final _Request<E, A> request,
                                                final Mailbox source, final A targetActor,
                                                final ResponseProcessor<E> responseProcessor) throws Exception {
        final JAMailbox sourceMailbox = (JAMailbox) source;
        if (!sourceMailbox.isRunning())
            throw new IllegalStateException(
                    "A valid source mailbox can not be idle");
        final Message message = sourceMailbox.createMessage(
                this != sourceMailbox
                        && mailboxFactory != sourceMailbox.getMailboxFactory(),
                inbox, request, targetActor, responseProcessor);
        addMessage(sourceMailbox, message, this == sourceMailbox ||
                (sourceMailbox != null && threadReference.get() == sourceMailbox.getThreadReference().get()));
    }

    /**
     * Returns true, if this message source is currently processing messages.
     */
    @Override
    public boolean isRunning() {
        return threadReference.get() != null;
    }

    @Override
    public final <E, A extends Actor> Message createMessage(
            final boolean _foreign, final MessageQueue inbox,
            final _Request<E, A> request, final A targetActor,
            final ResponseProcessor<E> responseProcessor) {
        return inbox.createMessage(_foreign, this, targetActor, currentMessage,
                request, exceptionHandler, responseProcessor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <E, A extends Actor> E call(final _Request<E, A> request,
                                             final A targetActor) throws Exception {
        final Caller caller = new Caller();
        final Message message = inbox.createMessage(true, caller, targetActor,
                null, request, null,
                (ResponseProcessor<E>) DummyResponseProcessor.SINGLETON);
        // Using a Caller means never local
        // Should we buffer here? (We don't atm) Buffering would be pointless!
        // What if another actor with the same mailbox is called by accident?
        // Don't we get a deadlock?  Yes. And developers can write infinite loops, too.
        // Sanity checks, if you add them, should be turned off in production.
        addMessage(null, message, false);
        return (E) caller.call();
    }

    @Override
    public final ExceptionHandler setExceptionHandler(
            final ExceptionHandler handler) {
        if (!isRunning())
            throw new IllegalStateException(
                    "Attempt to set an exception handler on an idle mailbox");
        final ExceptionHandler rv = this.exceptionHandler;
        this.exceptionHandler = handler;
        return rv;
    }

    private void addMessage(final MessageSource sourceMailbox,
                            final Message message, final boolean local) throws Exception {
        // sourceMailbox is either null, or running ...
        if ((sourceMailbox == null) || local
                || !sourceMailbox.buffer(message, this)) {
            addUnbufferedMessage(message, local);
        }
    }

    /**
     * Adds a message to the queue.
     */
    public void addUnbufferedMessage(final Message message, final boolean local)
            throws Exception {
        if (mailboxFactory.isClosing()) {
            if (message.isForeign() && message.isResponsePending())
                try {
                    message.close();
                } catch (final Throwable t) {
                }
            return;
        }
        inbox.offer(local, message);
        afterAdd();
    }

    /**
     * Adds messages to the queue.
     */
    @Override
    public void addUnbufferedMessages(final Queue<Message> messages)
            throws Exception {
        if (mailboxFactory.isClosing()) {
            final Iterator<Message> itm = messages.iterator();
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
        inbox.offer(messages);
        afterAdd();
    }

    /**
     * Should be called after adding some message(s) to the queue.
     */
    protected void afterAdd() throws Exception {
        /**
         * The compareAndSet method is a moderately expensive operation,
         * so we use a guard expression to reduce the number of times it is called.
         */
        if (threadReference.get() == null && inbox.isNonEmpty()) {
            mailboxFactory.submit(this, mayBlock);
        }
    }

    /**
     * Returns true, if the message could be buffered before sending.
     *
     * @param message Message to send-buffer
     * @return true, if buffered
     */
    @Override
    public boolean buffer(final Message message, final JAMailbox target) {
        if (mailboxFactory.isClosing())
            return false;
        ArrayDeque<Message> buffer;
        if (sendBuffer == null) {
            sendBuffer = new IdentityHashMap<JAMailbox, ArrayDeque<Message>>();
            buffer = null;
        } else {
            buffer = sendBuffer.get(target);
        }
        if (buffer == null) {
            buffer = new ArrayDeque<Message>(initialBufferSize);
            sendBuffer.put(target, buffer);
        }
        buffer.add(message);
        return true;
    }

    @Override
    public void run() {
        while (true) {
            final Message message = inbox.poll();
            if (message == null) {
                try {
                    onIdle();
                } catch (final MigrateException me) {
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
            if (mayBlock)
                try {
                    flush(true);
                } catch (final MigrateException me) {
                    throw me;
                } catch (Exception e) {
                    log.error("Exception thrown by onIdle", e);
                }
        }
    }

    /**
     * Called when all pending messages have been processed.
     */
    private void onIdle() throws Exception {
        if (onIdle != null) {
            flush(true);
            onIdle.run();
        }
        flush(true);
    }

    @Override
    public final boolean flush() throws Exception {
        return flush(false);
    }

    /**
     * Flushes buffered messages, if any.
     * Returns true if there was any.
     */
    public boolean flush(boolean mayMigrate) throws Exception {
        boolean result = false;
        if (sendBuffer != null) {
            final Iterator<Entry<JAMailbox, ArrayDeque<Message>>> iter = sendBuffer
                    .entrySet().iterator();
            while (iter.hasNext()) {
                result = true;
                final Entry<JAMailbox, ArrayDeque<Message>> entry = iter.next();
                final JAMailbox target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                if (!iter.hasNext() &&
                        mayMigrate &&
                        mayBlock &&
                        target instanceof MayBlockMailbox &&
                        getMailboxFactory() == target.getMailboxFactory() &&
                        !target.isRunning()) {
                    Thread currentThread = threadReference.get();
                    AtomicReference<Thread> targetThreadReference = target.getThreadReference();
                    if (targetThreadReference.get() == null &&
                            targetThreadReference.compareAndSet(null, currentThread)) {
                        while (!messages.isEmpty()) {
                            Message m = messages.poll();
                            target.addUnbufferedMessage(m, true);
                        }
                        throw new MigrateException(target);
                    }
                }
                target.addUnbufferedMessages(messages);
            }
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void processRequestMessage(final Message message) {
        if (message.isForeign())
            mailboxFactory.addAutoClosable(message);
        beforeProcessMessage(true, message);
        try {
            exceptionHandler = null; //NOPMD
            currentMessage = message;
            final _Request<?, Actor> request = message.getRequest();
            try {
                request.processRequest(message.getTargetActor(),
                        new Transport() {
                            @Override
                            public void processResponse(final Object response)
                                    throws Exception {
                                if (message.isForeign())
                                    mailboxFactory.removeAutoClosable(message);
                                if (!message.isResponsePending())
                                    return;
                                if (message.getResponseProcessor() != EventResponseProcessor.SINGLETON) {
                                    message.setResponse(response);
                                    message.getMessageSource()
                                            .incomingResponse(message,
                                                    BaseMailbox.this);
                                } else {
                                    if (response instanceof Throwable) {
                                        log.warn("Uncaught throwable",
                                                (Throwable) response);
                                    }
                                }
                            }

                            @Override
                            public MailboxFactory getMailboxFactory() {
                                MessageSource ms = message.getMessageSource();
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
                if (message.isForeign())
                    mailboxFactory.removeAutoClosable(message);
                processThrowable(t);
            }
        } finally {
            afterProcessMessage(true, message);
        }
    }

    private void processThrowable(final Throwable t) {
        if (!currentMessage.isResponsePending())
            return;
        final Message message = currentMessage;
        final _Request<?, Actor> req = message.getRequest();
        if (exceptionHandler != null) {
            try {
                exceptionHandler.processException(t);
            } catch (final Throwable u) {
                log.error("Exception handler unable to process throwable "
                        + exceptionHandler.getClass().getName(), u);
                if (!(message.getResponseProcessor() instanceof EventResponseProcessor)) {
                    if (!message.isResponsePending())
                        return;
                    currentMessage.setResponse(u);
                    message.getMessageSource().incomingResponse(message,
                            BaseMailbox.this);
                } else {
                    log.error("Thrown by exception handler and uncaught "
                            + exceptionHandler.getClass().getName(), t);
                }
            }
        } else {
            if (!message.isResponsePending())
                return;
            currentMessage.setResponse(t);
            if (!(message.getResponseProcessor() instanceof EventResponseProcessor))
                message.getMessageSource().incomingResponse(message,
                        BaseMailbox.this);
            else {
                log.warn("Uncaught throwable", t);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processResponseMessage(final Message message) {
        beforeProcessMessage(false, message);
        try {
            final Object response = message.getResponse();
            exceptionHandler = message.getSourceExceptionHandler();
            currentMessage = message.getOldMessage();
            if (response instanceof Throwable) {
                processThrowable((Throwable) response);
                return;
            }
            @SuppressWarnings("rawtypes")
            final ResponseProcessor responseProcessor = message
                    .getResponseProcessor();
            try {
                responseProcessor.processResponse(response);
            } catch (final Throwable t) {
                processThrowable(t);
            }
        } finally {
            afterProcessMessage(false, message);
        }
    }

    @Override
    public final void incomingResponse(final Message message,
                                       final JAMailbox responseSource) {
        try {
            addMessage(null, message, this == responseSource ||
                    (responseSource != null && threadReference.get() == responseSource.getThreadReference().get()));
        } catch (final Throwable t) {
            log.error("unable to add response message", t);
        }
    }

    @Override
    public JAMailboxFactory getMailboxFactory() {
        return mailboxFactory;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    /**
     * Called before running processXXXMessage(Message).
     */
    protected void beforeProcessMessage(final boolean request,
                                        final Message message) {
        // NOP
    }

    /**
     * Called after running processXXXMessage(Message).
     */
    protected void afterProcessMessage(final boolean request,
                                       final Message message) {
        // NOP
    }
}
