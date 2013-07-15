package org.agilewiki.jactor2.api;

/**
 * A _Request object is passed to another actor's mailbox for execution,
 * just as a Runnable is passed by SwingUtilities.invokeLater for execution on the UI thread.
 * <p>
 * Requests may either be bound to a target mailbox or unbound.
 * In the case of unbound requests, the target actor is specified when the _Request
 * is passed, and the target mailbox is determined
 * by calling getMailbox() on the target actor.
 * </p>
 *
 * @param <RESPONSE_TYPE>     the type of response to be returned.
 * @param <TARGET_ACTOR_TYPE> For Event's, this is the class of the target actor.
 *                            For Request's, this is the class Actor.
 */
public interface _Request<RESPONSE_TYPE, TARGET_ACTOR_TYPE> {

    /**
     * The processRequest method is always executed on the target actor's light-weight
     * thread (mailbox). Thread safety is not a requirement.
     *
     * @param _targetActor Null for Request objects,
     *                     this is the target actor for a Request.
     * @param _transport   A callback for processing the response on the appropriate thread.
     */
    public void processRequest(final TARGET_ACTOR_TYPE _targetActor,
                               final Transport<RESPONSE_TYPE> _transport) throws Exception;
}
