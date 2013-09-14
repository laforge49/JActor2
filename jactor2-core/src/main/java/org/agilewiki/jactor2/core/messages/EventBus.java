package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * Publishes events to subscribers.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * import org.agilewiki.jactor2.core.messaging.EventBus;
 * import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
 * import org.agilewiki.jactor2.core.threading.Facility;
 *
 * public class EventBusSample {
 *
 *     public static void main(final String[] _args) throws Exception {
 *         //Create a facility.
 *         Facility facility = new Facility();
 *         try {
 *             //Create a status logger blade.
 *             StatusLogger statusLogger =
 *                 new StatusLogger(new NonBlockingReactor(facility));
 *
 *             //Create a status printer blade.
 *             StatusPrinter statusPrinter = new StatusPrinter(facility);
 *
 *             //Define an event bus for StatusListener blades.
 *             EventBus&lt;StatusListener&gt; eventBus =
 *                 new EventBus&lt;StatusListener&gt;(new NonBlockingReactor(facility));
 *
 *             //Add statusLogger and statusPrinter to the subscribers of the event bus.
 *             eventBus.subscribeAReq(statusLogger).call();
 *             eventBus.subscribeAReq(statusPrinter).call();
 *
 *             //Send a status update to all subscribers.
 *             eventBus.publishAReq(new StatusUpdate("started")).call();
 *
 *             //Send a status update to all subscribers.
 *             eventBus.publishAReq(new StatusUpdate("stopped")).call();
 *         } finally {
 *             //Close the facility.
 *             facility.close();
 *         }
 *     }
 * }
 *
 * import org.agilewiki.jactor2.core.blades.Blade;
 *
 * //An interface for actors which process StatusUpdate events.
 * public interface StatusListener extends Blade {
 *     //Process a StatusUpdate event.
 *     void statusUpdate(final StatusUpdate _statusUpdate) throws Exception;
 * }
 *
 * import org.agilewiki.jactor2.core.messaging.Event;
 *
 * //An event sent to StatusListener blades.
 * public class StatusUpdate extends Event&lt;StatusListener&gt; {
 *     //The revised status.
 *     public final String newStatus;
 *
 *     //Create a StatusUpdate event.
 *     public StatusUpdate(final String _newStatus) {
 *         newStatus = _newStatus;
 *     }
 *
 *     //Invokes the statusUpdate method on a StatusListener blade.
 *     {@literal @}Override
 *     public void processEvent(StatusListener _targetBlade) throws Exception {
 *         _targetBlade.statusUpdate(this);
 *     }
 * }
 *
 * import org.agilewiki.jactor2.core.blades.BladeBase;
 * import org.agilewiki.jactor2.core.processing.Reactor;
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 *
 * //A blade which logs StatusUpdate events.
 * public class StatusLogger extends BladeBase implements StatusListener {
 *     //The logger.
 *     protected final Logger logger = LoggerFactory.getLogger(StatusLogger.class);
 *
 *     //Create a StatusLogger.
 *     public StatusLogger(final Reactor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 *
 *     //Logs the revised status.
 *     {@literal @}Override
 *     public void statusUpdate(final StatusUpdate _statusUpdate) {
 *         logger.info("new status: " + _statusUpdate.newStatus);
 *     }
 * }
 *
 * import org.agilewiki.jactor2.core.blades.BladeBase;
 * import org.agilewiki.jactor2.core.processing.IsolationReactor;
 * import org.agilewiki.jactor2.core.processing.Reactor;
 * import org.agilewiki.jactor2.core.threading.Facility;
 *
 * //A blade which prints status logger events.
 * public class StatusPrinter extends BladeBase implements StatusListener {
 *
 *     //Create an isolation StatusPrinter. (Isolation because the print may block the thread.)
 *     public StatusPrinter(final Facility _facility) throws Exception {
 *         Reactor messageProcessor = new IsolationReactor(_facility);
 *         initialize(messageProcessor);
 *     }
 *
 *     //Prints the revised status.
 *     {@literal @}Override
 *     public void statusUpdate(final StatusUpdate _statusUpdate) {
 *         System.out.println("new status: " + _statusUpdate.newStatus);
 *     }
 * }
 *
 * Output:
 * new status: started
 * new status: stopped
 * 16 [Thread-3] INFO org.agilewiki.jactor2.core.messaging.eventBus.StatusLogger - new status: started
 * 16 [Thread-3] INFO org.agilewiki.jactor2.core.messaging.eventBus.StatusLogger - new status: stopped
 * </pre>
 *
 * @param <TARGET_BLADE_TYPE> A subclass of Blade implemented by all subscribers and
 *                            the target of the published events.
 */
public class EventBus<TARGET_BLADE_TYPE extends Blade> extends BladeBase {
    /**
     * The blades which will receive the published events.
     */
    private final Set<TARGET_BLADE_TYPE> subscribers = new HashSet<TARGET_BLADE_TYPE>();

    /**
     * Create an event bus.
     *
     * @param _reactor The blade's reactor.
     */
    public EventBus(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }

    /**
     * Returns a request to add a subscriber.
     * The result of the request is true when the subscriber list was changed.
     *
     * @param _subscriber A blade that will receive the published events.
     * @return The request.
     */
    public AsyncRequest<Boolean> subscribeAReq(final TARGET_BLADE_TYPE _subscriber) {
        return new AsyncRequest<Boolean>(getReactor()) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                processAsyncResponse(subscribers.add(_subscriber));
            }
        };
    }

    /**
     * Returns a request to remove a subscriber.
     * The result of the request is true when the subscriber list was changed.
     *
     * @param _subscriber The blade that should no longer receive the published events.
     * @return The request.
     */
    public AsyncRequest<Boolean> unsubscribeAReq(final TARGET_BLADE_TYPE _subscriber) {
        return new AsyncRequest<Boolean>(getReactor()) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                processAsyncResponse(subscribers.remove(_subscriber));
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
    public AsyncRequest<Void> publishAReq(
            final Event<TARGET_BLADE_TYPE> event) {
        return new AsyncRequest<Void>(getReactor()) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                Iterator<TARGET_BLADE_TYPE> it = subscribers.iterator();
                while (it.hasNext()) {
                    event.signal(it.next());
                }
                processAsyncResponse(null);
            }
        };
    }
}