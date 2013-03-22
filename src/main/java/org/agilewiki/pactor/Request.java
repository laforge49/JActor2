package org.agilewiki.pactor;

/**
 * <p>
 * Request Object represents the User/Application Request which needs to be executed. The mailbox reference to which the Request 
 * should be send is set via the Constructor. 
 * </p>
 */
public abstract class Request<RESPONSE_TYPE> {
	
	/**
	 * The mailbox reference where this Request Objects is send for processing. The mailbox
	 * will be associated with the thread pool which will execute the processRequest method
	 * of the Request.
	 */
    private final Mailbox mailbox;

    public Request(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Mailbox getMailbox() {
        return mailbox;
    }

    /**
     * The processRequest is asynchronously invoked by the threads associated with the Requests attached mailbox. The send 
     * methods pushes the Request to the mailbox.
     * 
     * @param responseProcessor The ResponseProcessor contains the Response that is generated from the Request.
     * @throws Exception
     */
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
