package org.agilewiki.pactor;

abstract public class Request<RESPONSE_TYPE> {
    Mailbox mailbox;

    public Request(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    abstract public void processRequest(
            ResponseProcessorProcessor<ResponseProcessorProcessor> responseProcessor)
            throws Exception;

    public void send() throws Exception {
        mailbox.send(this);
    }

    public void send(Mailbox source, ResponseProcessorProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception {
        mailbox.send(this, source, responseProcessor);
    }

    public RESPONSE_TYPE pend() throws Exception {
        return (RESPONSE_TYPE) mailbox.pend(this);
    }
}
