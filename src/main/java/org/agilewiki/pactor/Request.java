package org.agilewiki.pactor;

abstract public class Request<RESPONSE_TYPE> {
    Mailbox mailbox;

    public Request(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    abstract public void processRequest(
            ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Throwable;

    public void send() throws Exception {
        mailbox.send(this);
    }

    public void reply(Mailbox source, ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception {
        mailbox.reply(this, source, responseProcessor);
    }

    public RESPONSE_TYPE pend() throws Throwable {
        return (RESPONSE_TYPE) mailbox.pend(this);
    }
}
