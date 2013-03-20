package org.agilewiki.pactor.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.agilewiki.pactor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MailboxImpl implements Mailbox, Runnable, MessageSource {
    private static Logger LOG = LoggerFactory.getLogger(MailboxImpl.class);;
    private final MailboxFactory mailboxFactory;
    private final Queue<Message> inbox = new ConcurrentLinkedQueue<Message>();
    private final AtomicBoolean running = new AtomicBoolean();
    private ExceptionHandler exceptionHandler;
    private Message currentMessage;

    public MailboxImpl(final MailboxFactory factory) {
        this.mailboxFactory = factory;
    }

    @Override
    public Mailbox createMailbox() {
        return mailboxFactory.createMailbox();
    }

    @Override
    public void addAutoClosable(final AutoCloseable closeable) {
        mailboxFactory.addAutoClosable(closeable);
    }

    @Override
    public void shutdown() {
        mailboxFactory.shutdown();
    }

    @Override
    public void send(final Request<?> request) throws Exception {
        final Message message = new Message(null, null,
                request, null, EventResponseProcessor.SINGLETON);
        addMessage(message);
    }

    @Override
    public <E> void reply(final Request<E> request, final Mailbox source,
            final ResponseProcessor<E> responseProcessor)
            throws Exception {
        final MailboxImpl sourceMailbox = (MailboxImpl) source;
        if (!sourceMailbox.running.get())
            throw new IllegalStateException(
                    "A valid source mailbox can not be idle");
        final Message message = new Message(sourceMailbox,
                sourceMailbox.currentMessage, request,
                sourceMailbox.exceptionHandler, responseProcessor);
        addMessage(message);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E pend(final Request<E> request) throws Exception {
        final Pender pender = new Pender();
        final Message message = new Message(pender, null,
                request, null, DummyResponseProcessor.SINGLETON);
        addMessage(message);
        return (E) pender.pend();
    }

    @Override
    public ExceptionHandler setExceptionHandler(final ExceptionHandler handler) {
        if (!running.get())
            throw new IllegalStateException(
                    "Attempt to set an exception handler on an idle mailbox");
        final ExceptionHandler rv = this.exceptionHandler;
        this.exceptionHandler = handler;
        return rv;
    }

    private void addMessage(final Message message) throws Exception {
        inbox.add(message);
        if (running.compareAndSet(false, true)) {
            if (inbox.peek() != null)
                mailboxFactory.submit(this);
            else
                running.set(false);
        }
    }

    @Override
    public void run() {
        while (true) {
            final Message message = inbox.poll();
            if (message == null) {
                running.set(false);
                if (inbox.peek() != null) {
                    if (!running.compareAndSet(false, true))
                        return;
                    continue;
                }
            }
            if (message.isResponsePending())
                processRequestMessage((Message) message);
            else
                processResponseMessage(message);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void processRequestMessage(final Message message) {
        exceptionHandler = null; //NOPMD
        currentMessage = message;
        final Request<?> request = message.getRequest();
        try {
            request.processRequest(new ResponseProcessor() {
                @Override
                public void processResponse(final Object response)
                        throws Exception {
                    if (!message.isResponsePending())
                        return;
                    message.setResponse(response);
                    if (!(message.getResponseProcessor() instanceof EventResponseProcessor)) {
                        message.getMessageSource().incomingResponse(message);
                    } else if (response instanceof Throwable) {
                        LOG.warn("Uncaught throwable", (Throwable) response);
                    }
                }
            });
        } catch (final Throwable t) {
            processThrowable(t);
        }
    }

    private void processThrowable(final Throwable t) {
        if (!currentMessage.isResponsePending())
            return;
        final Message message = currentMessage;
        if (exceptionHandler != null) {
            try {
                exceptionHandler.processException(t);
            } catch (final Throwable u) {
                LOG.error("Exception handler unable to process throwable "
                        + exceptionHandler.getClass().getName(), t);
                if (!(message.getResponseProcessor() instanceof EventResponseProcessor)) {
                    if (!message.isResponsePending())
                        return;
                    currentMessage.setResponse(u);
                    message.getMessageSource().incomingResponse(message);
                } else {
                    LOG.error("Thrown by exception handler and uncaught "
                            + exceptionHandler.getClass().getName(), t);
                }
            }
        } else {
            if (!message.isResponsePending())
                return;
            currentMessage.setResponse(t);
            if (!(message.getResponseProcessor() instanceof EventResponseProcessor))
                message.getMessageSource().incomingResponse(message);
            else {
                LOG.warn("Uncaught throwable", t);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processResponseMessage(final Message message) {
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
    }

    @Override
    public void incomingResponse(final Message message) {
        try {
            addMessage(message);
        } catch (final Throwable t) {
            LOG.error("unable to add response message", t);
        }
    }
}
