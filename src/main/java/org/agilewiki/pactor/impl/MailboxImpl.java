package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

public class MailboxImpl implements Mailbox {
    private MailboxFactory mailboxFactory;

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
        //todo
    }

    @Override
    public void send(Request request, ResponseProcessor responseProcessor)
            throws Exception {
        //todo
    }

    @Override
    public Object pend(Request request) throws Exception {
        return null;  //todo
    }
}
