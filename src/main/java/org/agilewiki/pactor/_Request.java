package org.agilewiki.pactor;

public interface _Request<RESPONSE_TYPE> {

    /** 
     * The processRequest is asynchronously invoked by the threads associated with the Requests attached mailbox. The send
     * methods pushes the Request to the mailbox.
     *
     * @param responseProcessor The ResponseProcessor contains the Response that is generated from the Request.
     * @throws Exception
     */
    public void processRequest(
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception;
}
