package org.agilewiki.pactor;

public abstract class RequestBase<RESPONSE_TYPE> implements Request<RESPONSE_TYPE> {
    private final Mailbox mailbox;

    public RequestBase(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }

    @Override
    public void send() throws Exception {
        mailbox.send(this);
    }

    @Override
    public void reply(final Mailbox source,
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception {
        mailbox.reply(this, source, responseProcessor);
    }

    @Override
    public RESPONSE_TYPE pend() throws Exception {
        return mailbox.pend(this);
    }
}
