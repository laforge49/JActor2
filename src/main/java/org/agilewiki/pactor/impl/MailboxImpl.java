package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MailboxImpl implements Mailbox, Runnable, MessageSource {
    private static Logger logger = LoggerFactory.getLogger(MailboxImpl.class);;
    private MailboxFactory mailboxFactory;
    private Queue<Message> inbox = new ConcurrentLinkedQueue<Message>();
    private AtomicBoolean running = new AtomicBoolean();
    public ExceptionHandler exceptionHandler;
    private RequestMessage currentRequestMessage;

    public MailboxImpl(MailboxFactory mailboxFactory) {
        this.mailboxFactory = mailboxFactory;
    }

    @Override
    public Mailbox createMailbox() {
        return mailboxFactory.createMailbox();
    }

    @Override
    public void addAutoClosable(AutoCloseable closeable) {
        mailboxFactory.addAutoClosable(closeable);
    }

    @Override
    public void shutdown() {
        mailboxFactory.shutdown();
    }

    @Override
    public void send(Request request) throws Exception {
        RequestMessage requestMessage = new RequestMessage(
                null, null, request, null, VoidResponseProcessor.singleton);
        addMessage(requestMessage);
    }

    @Override
    public void send(Request request, Mailbox source, ResponseProcessorInterface responseProcessor)
            throws Exception {
        MailboxImpl sourceMailbox = (MailboxImpl) source;
        RequestMessage requestMessage = new RequestMessage(
                sourceMailbox, sourceMailbox.currentRequestMessage, request, sourceMailbox.exceptionHandler, responseProcessor);
        addMessage(requestMessage);
    }

    private void addMessage(Message message) {
        inbox.add(message);
        if (running.compareAndSet(false, true)) {
            if (inbox.peek() != null)
                mailboxFactory.submit(this);
            else
                running.set(false);
        }
    }

    @Override
    public Object pend(Request request) throws Throwable {
        Pender pender = new Pender();
        RequestMessage requestMessage = new RequestMessage(
                pender, null, request, null, DummyResponseProcessor.singleton);
        addMessage(requestMessage);
        return pender.pend();
    }

    @Override
    public ExceptionHandler setExceptionHandler(ExceptionHandler exceptionHandler) {
        ExceptionHandler rv = this.exceptionHandler;
        this.exceptionHandler = exceptionHandler;
        return rv;
    }

    @Override
    public void run() {
        while (true) {
            Message message = inbox.remove();
            if (message == null) {
                running.set(false);
                if (inbox.peek() != null) {
                    if (!running.compareAndSet(false, true))
                        return;
                    continue;
                }
            }
            if (message instanceof RequestMessage)
                processRequestMessage((RequestMessage) message);
            else
                processResponseMessage((ResponseMessage) message);
        }
    }

    private void processRequestMessage(final RequestMessage requestMessage) {
        exceptionHandler = null;
        currentRequestMessage = requestMessage;
        Request request = requestMessage.request;
        try {
            request.processRequest(new ResponseProcessor() {
                @Override
                public void processResponse(Object response) throws Exception {
                    if (!requestMessage.active)
                        return;
                    requestMessage.active = false;
                    if (requestMessage.responseProcessor.responseRequired())
                        requestMessage.messageSource.incomingResponse(requestMessage, response);
                    else if (response instanceof Throwable) {
                        logger.warn("Uncaught throwable", (Throwable) response);
                    }
                }
            });
        } catch (Throwable t) {
            processThrowable(t);
        }
    }

    private void processThrowable(Throwable t) {
        if (!currentRequestMessage.active)
            return;
        if (exceptionHandler != null) {
            try {
                exceptionHandler.processException(t);
            } catch (Throwable u) {
                logger.error("Exception handler unable to process throwable " +
                        exceptionHandler.getClass().getName(), (Throwable) t);
                if (currentRequestMessage.responseProcessor.responseRequired()) {
                    if (!currentRequestMessage.active)
                        return;
                    currentRequestMessage.active = false;
                    currentRequestMessage.messageSource.incomingResponse(currentRequestMessage, u);
                } else {
                    logger.error("Thrown by exception handler and uncaught " +
                            exceptionHandler.getClass().getName(), (Throwable) t);
                }
            }
        } else {
            if (!currentRequestMessage.active)
                return;
            currentRequestMessage.active = false;
            if (currentRequestMessage.responseProcessor.responseRequired())
                currentRequestMessage.messageSource.incomingResponse(currentRequestMessage, t);
            else {
                logger.warn("Uncaught throwable", (Throwable) t);
            }
        }
    }

    private void processResponseMessage(ResponseMessage responseMessage) {
        RequestMessage requestMessage = responseMessage.requestMessage;
        Object response = responseMessage.response;
        exceptionHandler = requestMessage.sourceExceptionHandler;
        currentRequestMessage = requestMessage.oldRequestMessage;
        if (response instanceof Throwable) {
            processThrowable((Throwable) response);
            return;
        }
        ResponseProcessorInterface responseProcessor = requestMessage.responseProcessor;
        try {
            responseProcessor.processResponse(response);
        } catch (Throwable t) {
            processThrowable(t);
        }
    }

    @Override
    public void incomingResponse(RequestMessage requestMessage, Object response) {
        ResponseMessage responseMessage = new ResponseMessage(requestMessage, response);
        addMessage(responseMessage);
    }
}
