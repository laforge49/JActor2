package org.agilewiki.pactor;

/**
 * <p>
 * Request Object represents the User/Application Request which needs to be executed. The mailbox reference to which the Request
 * should be send is set via the Constructor.
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
