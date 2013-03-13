package org.agilewiki.pactor.impl;

import org.agilewiki.pactor.Mailbox;

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
}
