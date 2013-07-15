package org.agilewiki.jactor2.api;

/**
 * A BoundRequest Object implements an operation to be performed. The BoundRequest is bound to a Mailbox and
 * will be processed by the thread owned by that Mailbox.
 *
 * @param <RESPONSE_TYPE> The class of the result returned when this BoundRequest is processed.
 */
@Deprecated
public interface BoundRequest<RESPONSE_TYPE> {

    /**
     * Returns the Mailbox to which this BoundRequest is bound and to which this BoundRequest is to be passed.
     *
     * @return The target Mailbox.
     */
    public Mailbox getMailbox();

    /**
     * Passes this BoundRequest to the target Mailbox without a return address.
     * No result is passed back and if an exception is thrown while processing this BoundRequest,
     * that exception is simply logged as a warning.
     */
    public void signal() throws Exception;

    /**
     * Passes this BoundRequest together with the ResponseProcessor to the target Mailbox.
     *
     * @param _source The mailbox on whose thread this method was invoked and which
     *                will buffer this BoundRequest and subsequently receive the result for
     *                processing on the same thread.
     * @param _rp     Passed with this request and then returned with the result, the
     *                ResponseProcessor is used to process the result on the same thread
     *                that originally invoked this method.
     */
    public void send(final Mailbox _source,
                     final ResponseProcessor<RESPONSE_TYPE> _rp) throws Exception;

    /**
     * Passes this BoundRequest to the target Mailbox and blocks the current thread until
     * a result is returned.
     *
     * @return The result from processing this BoundRequest.
     * @throws Exception If the result is an exception, it is thrown rather than being returned.
     */
    public RESPONSE_TYPE call() throws Exception;

    /**
     * The processRequest method will be invoked by the target Mailbox on its own thread
     * when this BoundRequest is received for processing.
     *
     * @param _transport The Transport that is responsible for passing the result back
     *                   to the originator of this BoundRequest. Either an Exception must be thrown or
     *                   the _rp.processResponse method must be invoked.
     */
    public void processRequest(final Transport<RESPONSE_TYPE> _transport)
            throws Exception;
}
