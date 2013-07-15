package org.agilewiki.jactor2.api;

/**
 * An UnboundRequest implements an operation that is to be performed on an actor in a thread-safe way,
 * without having been bound to a specific target.
 *
 * @param <RESPONSE_TYPE>     The class of the result returned when this UnboundRequest is processed.
 * @param <TARGET_ACTOR_TYPE> The class of the actor that will be used when this UnboundRequest is processed.
 */
public interface UnboundRequest<RESPONSE_TYPE, TARGET_ACTOR_TYPE extends Actor>
        extends _Request<RESPONSE_TYPE, TARGET_ACTOR_TYPE> {

    /**
     * Passes this BoundRequest to the target Mailbox without a return address.
     * No result is passed back and if an exception is thrown while processing this BoundRequest,
     * that exception is simply logged as a warning.
     *
     * @param _targetActor The actor being operated on.
     */
    public void signal(final TARGET_ACTOR_TYPE _targetActor) throws Exception;

    /**
     * Passes this BoundRequest to the target actor's Mailbox without a return address.
     * No result is passed back and if an exception is thrown while processing this BoundRequest,
     * that exception is simply logged as a warning.
     *
     * @param _source      The mailbox on whose thread this method was invoked and which
     *                     will buffer this BoundRequest.
     * @param _targetActor The actor being operated on.
     */
    public void signal(final Mailbox _source,
                       final TARGET_ACTOR_TYPE _targetActor) throws Exception;

    /**
     * Passes this BoundRequest together with the ResponseProcessor to the target actor's Mailbox.
     *
     * @param _source      The mailbox on whose thread this method was invoked and which
     *                     will buffer this BoundRequest and subsequently receive the result for
     *                     processing on the same thread.
     * @param _targetActor The actor being operated on.
     * @param _rp          Passed with this request and then returned with the result, the
     *                     ResponseProcessor is used to process the result on the same thread
     *                     that originally invoked this method.
     */
    public void send(final Mailbox _source,
                     final TARGET_ACTOR_TYPE _targetActor,
                     final ResponseProcessor<RESPONSE_TYPE> _rp) throws Exception;

    /**
     * Passes this BoundRequest to the target actor's Mailbox and blocks the current thread until
     * a result is returned.
     *
     * @param _targetActor The actor being operated on.
     * @return The result from processing this BoundRequest.
     * @throws Exception If the result is an exception, it is thrown rather than being returned.
     */
    public RESPONSE_TYPE call(final TARGET_ACTOR_TYPE _targetActor)
            throws Exception;
}
