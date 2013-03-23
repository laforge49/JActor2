package org.agilewiki.pactor;

public interface Request<RESPONSE_TYPE> {

    public Mailbox getMailbox();

    public void send() throws Exception;

    public void reply(final Mailbox source,
                      final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception;

    public RESPONSE_TYPE pend() throws Exception;

    public abstract void processRequest(
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception;
}
