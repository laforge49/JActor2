package org.agilewiki.pactor;

abstract public class Request<RESPONSE_TYPE> {
    private Mailbox mailbox;

    public Request(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public Mailbox getMailbox() {
        return mailbox;
    }

    abstract public void processRequest(
            ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Throwable;

    public void send() throws Throwable {
        mailbox.send(this);
    }

    public void reply(Mailbox source, ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Throwable {
        mailbox.reply(this, source, responseProcessor);
    }

    public RESPONSE_TYPE pend() throws Throwable {
        return (RESPONSE_TYPE) mailbox.pend(this);
    }
}
