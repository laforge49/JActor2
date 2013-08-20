package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.Actor;
import org.agilewiki.jactor2.core.ActorBase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements Publish and Subscribe.
 *
 * @param <TARGET_ACTOR_TYPE> The type of subscriber.
 */
public class EventBus<TARGET_ACTOR_TYPE extends Actor> extends ActorBase {
    /**
     * The subscribers
     */
    private final Set<TARGET_ACTOR_TYPE> subscribers = new HashSet<TARGET_ACTOR_TYPE>();

    /**
     * A request to add a subscriber.
     * The result of the request is true when the subscriber list was changed.
     *
     * @param _subscriber The actor to be added.
     * @return The request.
     */
    public Request<Boolean> subscribeReq(final TARGET_ACTOR_TYPE _subscriber) {
        return new Request<Boolean>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Boolean> _rp)
                    throws Exception {
                _rp.processResponse(subscribers.add(_subscriber));
            }
        };
    }

    /**
     * A request to remove a subscriber.
     * The result of the request is true when the subscriber list was changed.
     *
     * @param _subscriber The actor to be removed.
     * @return The request.
     */
    public Request<Boolean> unsubscribeReq(final TARGET_ACTOR_TYPE _subscriber) {
        return new Request<Boolean>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Boolean> _rp)
                    throws Exception {
                _rp.processResponse(subscribers.remove(_subscriber));
            }
        };
    }

    /**
     * A request to publish an unbound request to all the subscribers.
     * The request completes with a null result only when all subscribers have processed the
     * unbound request.
     * Exceptions thrown by subscribers when processign the unbound request are simply ignored.
     *
     * @param event The request to be published.
     * @return The request.
     */
    public Request<Void> publishReq(
            final Event<TARGET_ACTOR_TYPE> event) {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Void> _rp)
                    throws Exception {
                Iterator<TARGET_ACTOR_TYPE> it = subscribers.iterator();
                while (it.hasNext()) {
                    event.signal(it.next());
                }
                _rp.processResponse(null);
            }
        };
    }
}