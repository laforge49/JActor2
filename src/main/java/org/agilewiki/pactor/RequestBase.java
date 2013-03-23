package org.agilewiki.pactor;

/**
 * <p>
 * Request Object represents the User/Application Request which needs to be executed. The mailbox reference to which the Request
 * should be send is set via the Constructor.
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

    /**
     * The processRequest is asynchronously invoked by the threads associated with the Requests attached mailbox. The send
     * methods pushes the Request to the mailbox.
     *
     * @param responseProcessor The ResponseProcessor contains the Response that is generated from the Request.
     * @throws Exception
     */
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
