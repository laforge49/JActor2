package org.agilewiki.pactor.util;

import org.agilewiki.pactor.api.*;

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
     * A request to add a subscriber.
     * The result of the request is true when the subscriber list was changed.
     *
     * @param _subscriber The actor to be added.
     * @return The request.
     */
    public Request<Boolean> subscribeReq(final TARGET_ACTOR_TYPE _subscriber) {
        return new RequestBase<Boolean>(getMailbox()) {
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
        return new RequestBase<Boolean>(getMailbox()) {
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
     * @param unboundRequest The request to be published.
     * @return The request.
     */
    public Request<Void> publishReq(
            final UnboundRequest<Void, TARGET_ACTOR_TYPE> unboundRequest) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(final Transport<Void> _rp)
                    throws Exception {
                final Object[] subs = subscribers.toArray();
                final ResponseCounter<Void> rc = new ResponseCounter<Void>(
                        subs.length, null, _rp);
                getMailbox().setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        rc.decrementCount();
                    }
                });
                for (final Object object : subs) {
                    @SuppressWarnings("unchecked")
                    final TARGET_ACTOR_TYPE subscriber = (TARGET_ACTOR_TYPE) object;
                    unboundRequest.send(getMailbox(), subscriber, rc);
                }
            }
        };
    }
}
