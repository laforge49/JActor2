package org.agilewiki.pactor;

/**
 * <p>
 * Request Object represents the User/Application data that needs to be processed. 
 * Request abstracts the Application/User Request(data) that is dispatched to the Actor's mailbox for asynchronous execution.
 * The Request object should be created in the PActor, it abstracts the user/application data that needs to be processed 
 * by the lightweight thread attached to the Actors mailbox.
 * </p>
 */
public abstract class Request<RESPONSE_TYPE> {
	
	/**
	 * The mailbox reference where this Request Objects is dispatched. The mailbox
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
     * The processRequest is asynchronously invoked by the threads associated with the Request's attached mailbox. The send 
     * methods pushes the Request to the mailbox.
     * 
     * @param responseProcessor The ResponseProcessor contains the Response that is generated from the Request.
     * @throws Exception
     */
    public abstract void processRequest(
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception;

    /**
     * This will send the current Request to the mailbox for asynchronous processing.
     * 
     * @throws Exception 
     */
    public void send() throws Exception {
        mailbox.send(this);
    }

    /**
     * 
     * @param source The mailbox associated with the Request for which the ResponseMessage is to 
     * added for asynchronous processing.
     * 
     * @param responseProcessor The associated ResponseProcessor whose role is to process the response.
     * @throws Exception Will thrown Exception if the source mailbox is not running.
     */
    public void reply(final Mailbox source,
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception {
        mailbox.reply(this, source, responseProcessor);
    }

    /**
     * This will make the invoking thread to wait for the response before continuing ahead.
     * It will let the invocation to be synchronous for the calling thread. It is better to evaluate 
     * if plain OO call would for using instead of pend.
     * 
     * @return RESPONSE_TYPE
     * @throws Exception
     */
    public RESPONSE_TYPE pend() throws Exception {
        return mailbox.pend(this);
    }
}
