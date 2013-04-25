package org.agilewiki.pamailbox;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.agilewiki.pactor.*;
import org.slf4j.Logger;

public class MailboxImpl implements PAMailbox, Runnable {

    private final Logger log;

    private final PAMailboxFactory mailboxFactory;
    private final MessageQueue inbox;
    private final AtomicBoolean running = new AtomicBoolean();
    private final Runnable onIdle;
    private final Runnable messageProcessor;
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
    private Map<PAMailbox, ArrayDeque<Message>> sendBuffer;

    private ExceptionHandler exceptionHandler;
    private Message currentMessage;

    /**
     * messageQueue can be null to use the default queue implementation.
     */
    public MailboxImpl(final boolean _mayBlock, final Runnable _onIdle,
            final Runnable _messageProcessor, final PAMailboxFactory factory,
            final MessageQueue messageQueue, final Logger _log,
            final int _initialBufferSize) {
        mayBlock = _mayBlock;
        onIdle = _onIdle;
        messageProcessor = _messageProcessor;
        running.set(messageProcessor != null);
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
        final Iterator<Entry<PAMailbox, ArrayDeque<Message>>> iter = sendBuffer
                .entrySet().iterator();
        while (iter.hasNext()) {
            final Entry<PAMailbox, ArrayDeque<Message>> entry = iter.next();
            final PAMailbox target = entry.getKey();
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

    /**
     * Flushes buffered messages, if any.
     * Returns true if there was any.
     *
     * @throws Exception
     */
    @Override
    public final boolean flush() throws Exception {
        boolean result = false;
        if (sendBuffer != null) {
            final Iterator<Entry<PAMailbox, ArrayDeque<Message>>> iter = sendBuffer
                    .entrySet().iterator();
            while (iter.hasNext()) {
                result = true;
                final Entry<PAMailbox, ArrayDeque<Message>> entry = iter.next();
                final PAMailbox target = entry.getKey();
                final ArrayDeque<Message> messages = entry.getValue();
                iter.remove();
                target.addUnbufferedMessages(messages);
            }
        }
        return result;
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
        addMessage(sourceMailbox, message, this == source);
    }

    @Override
    public final <E, A extends Actor> void send(final _Request<E, A> request,
            final Mailbox source, final A targetActor,
            final ResponseProcessor<E> responseProcessor) throws Exception {
        final PAMailbox sourceMailbox = (PAMailbox) source;
        if (!sourceMailbox.isRunning())
            throw new IllegalStateException(
                    "A valid source mailbox can not be idle");
        final Message message = sourceMailbox.createMessage(
                this != sourceMailbox
                        && mailboxFactory != sourceMailbox.getMailboxFactory(),
                inbox, request, targetActor, responseProcessor);
        addMessage(sourceMailbox, message, this == sourceMailbox);
    }

    /**
     * Returns true, if this message source is currently processing messages.
     */
    @Override
    public final boolean isRunning() {
        return running.get();
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
        if (!running.get())
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
    private void addUnbufferedMessage(final Message message, final boolean local)
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
    private void afterAdd() throws Exception {
        /**
         * The compareAndSet method is a moderately expensive operation,
         * so we use a guard expression to reduce the number of times it is called.
         */
        if (!running.get() && running.compareAndSet(false, true)) {
            if (inbox.isNonEmpty())
                mailboxFactory.submit(this, mayBlock);
            else
                running.set(false);
        } else if (messageProcessor != null)
            messageProcessor.run();
    }

    /**
     * Returns true, if the message could be buffered before sending.
     *
     * @param message Message to send-buffer
     * @return true, if buffered
     */
    @Override
    public boolean buffer(final Message message, final PAMailbox target) {
        if (mailboxFactory.isClosing())
            return false;
        ArrayDeque<Message> buffer;
        if (sendBuffer == null) {
            sendBuffer = new IdentityHashMap<PAMailbox, ArrayDeque<Message>>();
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
    public final void run() {
        if (messageProcessor != null)
            while (true) {
                final Message message = inbox.poll();
                if (message == null)
                    if (onIdle())
                        return;
                if (message.isResponsePending())
                    processRequestMessage(message);
                else
                    processResponseMessage(message);
            }
        else
            while (true) {
                final Message message = inbox.poll();
                if (message == null) {
                    if (!onIdle())
                        continue;
                    running.set(false);
                    // If inbox.isNonEmpty() was ever to throw an Exception,
                    // we should still be in a consistent state, since there
                    // was no unprocessed message, and running was set to false.
                    if (inbox.isNonEmpty()) {
                        if (!running.compareAndSet(false, true))
                            return;
                        continue;
                    }
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
    private boolean onIdle() {
        try {
            flush();
        } catch (final Throwable t) {
            log.error("Exception thrown by flush", t);
        }
        if (onIdle != null) {
            onIdle.run();
            return !inbox.isNonEmpty();
        }
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
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
                                                    MailboxImpl.this);
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
                        + exceptionHandler.getClass().getName(), t);
                if (!(message.getResponseProcessor() instanceof EventResponseProcessor)) {
                    if (!message.isResponsePending())
                        return;
                    currentMessage.setResponse(u);
                    message.getMessageSource().incomingResponse(message,
                            MailboxImpl.this);
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
                        MailboxImpl.this);
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
            final PAMailbox responseSource) {
//        final MailboxImpl sourceMailbox = (MailboxImpl) responseSource;
//        if (!sourceMailbox.running.get())
//            throw new IllegalStateException(
//                    "A valid source mailbox can not be idle");
        try {
            addMessage(null, message, this == responseSource);
        } catch (final Throwable t) {
            log.error("unable to add response message", t);
        }
    }

    @Override
    public PAMailboxFactory getMailboxFactory() {
        return mailboxFactory;
    }

    @Override
    public PAMailbox createPort(final Mailbox _source, final int size) {
        return this;
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
