package org.agilewiki.pactor;

abstract public class Request<RESPONSE_TYPE> {
    Mailbox mailbox;

    public Request(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    abstract public void processRequest(
            ResponseProcessor<ResponseProcessor> responseProcessor)
            throws Exception;

    public void send() throws Exception {
        mailbox.send(this);
    }

    public void send(ResponseProcessor<RESPONSE_TYPE> responseProcessor)
        throws Exception {
        mailbox.send(this, responseProcessor);
    }

    public RESPONSE_TYPE pend() throws Exception {
        return (RESPONSE_TYPE) mailbox.pend(this);
    }
}
