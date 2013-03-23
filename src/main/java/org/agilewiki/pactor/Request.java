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

    public void send() throws Exception;

    public void reply(final Mailbox source,
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception;

    public RESPONSE_TYPE pend() throws Exception;

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
}
