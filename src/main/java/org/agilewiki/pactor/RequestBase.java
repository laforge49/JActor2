package org.agilewiki.pactor;

/**
 * <p>
 * The basic implementation of the Request interface. The application should extend the RequestBase to
 * create the Request implementations which would be used to send to the PActors mailbox for asynchronous
 * processing.
 * </p>
 */
public abstract class RequestBase<RESPONSE_TYPE> implements
        Request<RESPONSE_TYPE> {
    /**
     * The mailbox reference where this Request Objects is send for processing. The mailbox
     * will be associated with the thread pool which will execute the processRequest method
     * of the Request.
     */
    private final Mailbox mailbox;

    public RequestBase(final Mailbox mbox) {
        if (mbox == null) {
            throw new NullPointerException("mbox");
        }
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
    public void send(final Mailbox source) throws Exception {
        mailbox.send(this, source);
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
