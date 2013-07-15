package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.api.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements Publish and Subscribe.
 *
 * @param <TARGET_ACTOR_TYPE> The type of subscriber.
 */
public class Publisher<TARGET_ACTOR_TYPE extends Actor> extends ActorBase {
    /**
     * The subscribers
     */
    private final Set<TARGET_ACTOR_TYPE> subscribers = new HashSet<TARGET_ACTOR_TYPE>();

    /**
     * A boundRequest to add a subscriber.
     * The result of the boundRequest is true when the subscriber list was changed.
     *
     * @param _subscriber The actor to be added.
     * @return The boundRequest.
     */
    public BoundRequest<Boolean> subscribeReq(final TARGET_ACTOR_TYPE _subscriber) {
        return new BoundRequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Boolean> _rp)
                    throws Exception {
                _rp.processResponse(subscribers.add(_subscriber));
            }
        };
    }

    /**
     * A boundRequest to remove a subscriber.
     * The result of the boundRequest is true when the subscriber list was changed.
     *
     * @param _subscriber The actor to be removed.
     * @return The boundRequest.
     */
    public BoundRequest<Boolean> unsubscribeReq(final TARGET_ACTOR_TYPE _subscriber) {
        return new BoundRequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Boolean> _rp)
                    throws Exception {
                _rp.processResponse(subscribers.remove(_subscriber));
            }
        };
    }

    /**
     * A boundRequest to publish an unbound boundRequest to all the subscribers.
     * The boundRequest completes with a null result only when all subscribers have processed the
     * unbound boundRequest.
     * Exceptions thrown by subscribers when processign the unbound boundRequest are simply ignored.
     *
     * @param event The boundRequest to be published.
     * @return The boundRequest.
     */
    public BoundRequest<Void> publishReq(
            final Event<Void, TARGET_ACTOR_TYPE> event) {
        return new BoundRequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Void> _rp)
                    throws Exception {
                final Object[] subs = subscribers.toArray();
                for (final Object object : subs) {
                    @SuppressWarnings("unchecked")
                    final TARGET_ACTOR_TYPE subscriber = (TARGET_ACTOR_TYPE) object;
                    event.signal(subscriber);
                }
                _rp.processResponse(null);
            }
        };
    }
}
