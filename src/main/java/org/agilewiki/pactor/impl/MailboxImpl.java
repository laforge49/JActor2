package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public final class MailboxImpl implements Mailbox, Runnable {
    private MailboxFactory mailboxFactory;
    private Queue<Message> inbox = new ConcurrentLinkedQueue<Message>();
    private AtomicReference<MailboxImpl> atomicControl = new AtomicReference<MailboxImpl>();
    ExceptionHandler exceptionHandler;

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
        send(request, null, VoidResponseProcessorProcessor.singleton);
    }

    @Override
    public void send(Request request, Mailbox source, ResponseProcessorInterface responseProcessor)
            throws Exception {
        MailboxImpl sourceMailbox = (MailboxImpl) source;
        RequestMessage requestMessage = new RequestMessage(
                sourceMailbox, this, request, sourceMailbox.exceptionHandler, responseProcessor);
        addMessage(requestMessage);
    }

    private void addMessage(Message message) {
        inbox.add(message);
        if (atomicControl.compareAndSet(null, this)) {
            if (inbox.peek() != null)
                mailboxFactory.submit(this);
            else
                atomicControl.set(null);
        }
    }

    @Override
    public Object pend(Request request) throws Exception {
        return null;  //todo
    }

    @Override
    public ExceptionHandler setExceptionHandler(ExceptionHandler exceptionHandler) {
        return null;  //todo
    }

    @Override
    public void run() {
        while (true) {
            Message message = inbox.remove();
            if (message == null) {
                atomicControl.set(null);
                if (inbox.peek() != null) {
                    if (!atomicControl.compareAndSet(null, this))
                        return;
                    continue;
                }
            }
            processMessage(message);
        }
    }

    private void processMessage(Message message) {
        //todo
    }
}
