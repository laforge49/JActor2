package org.agilewiki.jactor2.api;

/**
 * An Event implements an operation that is to be performed on an actor in a thread-safe way,
 * without having been bound to a specific target actor.
 *
 * @param <TARGET_ACTOR_TYPE> The class of the actor that will be used when this Event is processed.
 */
public interface Event<TARGET_ACTOR_TYPE extends Actor>
        extends _Request<Void, TARGET_ACTOR_TYPE> {

    /**
     * Passes this Request to the target Mailbox without a return address.
     * No result is passed back and if an exception is thrown while processing this Request,
     * that exception is simply logged as a warning.
     *
     * @param _targetActor The actor being operated on.
     */
    public void signal(final TARGET_ACTOR_TYPE _targetActor) throws Exception;

    /**
     * The processRequest method will be invoked by the target Mailbox on its own thread
     * when this Request is received for processing.
     *
     * @param _targetActor The target actor for an Event.
     */
    public void processSignal(final TARGET_ACTOR_TYPE _targetActor)
            throws Exception;
}
