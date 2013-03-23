package org.agilewiki.pactor;

public abstract class RequestBase<RESPONSE_TYPE> {
    private final Mailbox mailbox;

    public RequestBase(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Mailbox getMailbox() {
        return mailbox;
    }

    public abstract void processRequest(
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception;

    public void send() throws Exception {
        mailbox.send(this);
    }

    public void reply(final Mailbox source,
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception {
        mailbox.reply(this, source, responseProcessor);
    }

    public RESPONSE_TYPE pend() throws Exception {
        return mailbox.pend(this);
    }
}
