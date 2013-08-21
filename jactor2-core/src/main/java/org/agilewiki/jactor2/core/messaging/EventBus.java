package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.Actor;
import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Publishes events to subscribers.
 *
 * @param <TARGET_ACTOR_TYPE> A subclass of Actor implemented by all subscribers and
 *                            the target of the published events.
 */
public class EventBus<TARGET_ACTOR_TYPE extends Actor> extends ActorBase {
    /**
     * The actors which will receive the published events.
     */
    private final Set<TARGET_ACTOR_TYPE> subscribers = new HashSet<TARGET_ACTOR_TYPE>();

    /**
     * Create an event bus.
     *
     * @param _messageProcessor The actor's message processor.
     */
    public EventBus(final MessageProcessor _messageProcessor) throws Exception {
        initialize(_messageProcessor);
    }

    /**
     * Returns a request to add a subscriber.
     * The result of the request is true when the subscriber list was changed.
     *
     * @param _subscriber An actor that will receive the published events.
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
     * Returns a request to remove a subscriber.
     * The result of the request is true when the subscriber list was changed.
     *
     * @param _subscriber The actor that should no longer receive the published events.
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
     * Returns a request to publish an event to all the subscribers.
     * The request completes with a null result only when the event has been sent to all subscribers.
     * Exceptions thrown by subscribers when processing these events are are simply logged,
     * as is the case for all events.
     *
     * @param event The event to be published.
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