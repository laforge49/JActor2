package org.agilewiki.pactor;

/**
 * <p>
 * Request Object represents the User/Application data that needs to be processed. 
 * Request abstracts the Application/User Request(data) that is dispatched to the Actor's mailbox for asynchronous execution.
 * The Request object should be created in the PActor, it abstracts the user/application data that needs to be processed 
 * by the lightweight thread attached to the Actors mailbox.
 * </p>
 */

public interface Request<RESPONSE_TYPE> {

    public Mailbox getMailbox();

    /**
     * This will signal the current Request to the mailbox for asynchronous processing.
     * 
     */
    public void signal() throws Exception;

    public void signal(final Mailbox source) throws Exception;

    /**
     * reply will be used when chain of PActors needs to process the User/Application Request.
     * The responseProcessor would be shared for PActor chain.
     * 
     * @param source The mailbox associated with the Request for which the ResponseMessage is to 
     * added for asynchronous processing.
     * 
     * @param responseProcessor The associated ResponseProcessor whose role is to process the response.
     * @throws Exception Will thrown Exception if the source mailbox is not running.
     */ 
    public void reply(final Mailbox source,
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception;

    /**
     * This will make the invoking thread to wait for the response before continuing ahead.
     * It will let the invocation to be synchronous for the calling thread. It is better to evaluate 
     * if plain OO call would for using instead of call.
     * 
     * @return RESPONSE_TYPE
     * @throws Exception
     */ 
    public RESPONSE_TYPE call() throws Exception;

    /**
     * The processRequest is asynchronously invoked by the threads associated with the Requests attached mailbox. The signal
     * methods pushes the Request to the mailbox.
     *
     * @param responseProcessor The ResponseProcessor contains the Response that is generated from the Request.
     * @throws Exception
     */
    public void processRequest(
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception;
}
