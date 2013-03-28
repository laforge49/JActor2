package org.agilewiki.pactor;

public interface _Request<RESPONSE_TYPE, TARGET_ACTOR_TYPE> {

    /** 
     * The processRequest is asynchronously invoked by the threads associated with the Requests attached mailbox. The signal
     * methods pushes the Request to the mailbox.
     *
     * @param responseProcessor The ResponseProcessor contains the Response that is generated from the Request.
     * @throws Exception
     */
    public void processRequest(
            final TARGET_ACTOR_TYPE _targetActor,
            final ResponseProcessor<RESPONSE_TYPE> responseProcessor)
            throws Exception;
}
