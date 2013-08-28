package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.Actor;
import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

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
 * import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
 * import org.agilewiki.jactor2.core.threading.ModuleContext;
 *
 * public class EventBusSample {
 *
 *     public static void main(final String[] _args) throws Exception {
 *         //Create a module context.
 *         ModuleContext moduleContext = new ModuleContext();
 *         try {
 *             //Create a status logger actor.
 *             StatusLogger statusLogger =
 *                 new StatusLogger(new NonBlockingMessageProcessor(moduleContext));
 *
 *             //Create a status printer actor.
 *             StatusPrinter statusPrinter = new StatusPrinter(moduleContext);
 *
 *             //Define an event bus for StatusListener actors.
 *             EventBus&lt;StatusListener&gt; eventBus =
 *                 new EventBus&lt;StatusListener&gt;(new NonBlockingMessageProcessor(moduleContext));
 *
 *             //Add statusLogger and statusPrinter to the subscribers of the event bus.
 *             eventBus.subscribeReq(statusLogger).call();
 *             eventBus.subscribeReq(statusPrinter).call();
 *
 *             //Send a status update to all subscribers.
 *             eventBus.publishReq(new StatusUpdate("started")).call();
 *
 *             //Send a status update to all subscribers.
 *             eventBus.publishReq(new StatusUpdate("stopped")).call();
 *         } finally {
 *             //Close the module context.
 *             moduleContext.close();
 *         }
 *     }
 * }
 *
 * import org.agilewiki.jactor2.core.Actor;
 *
 * //An interface for actors which process StatusUpdate events.
 * public interface StatusListener extends Actor {
 *     //Process a StatusUpdate event.
 *     void statusUpdate(final StatusUpdate _statusUpdate) throws Exception;
 * }
 *
 * import org.agilewiki.jactor2.core.messaging.Event;
 *
 * //An event sent to StatusListener actors.
 * public class StatusUpdate extends Event&lt;StatusListener&gt; {
 *     //The revised status.
 *     public final String newStatus;
 *
 *     //Create a StatusUpdate event.
 *     public StatusUpdate(final String _newStatus) {
 *         newStatus = _newStatus;
 *     }
 *
 *     //Invokes the statusUpdate method on a StatusListener actor.
 *     {@literal @}Override
 *     public void processEvent(StatusListener _targetActor) throws Exception {
 *         _targetActor.statusUpdate(this);
 *     }
 * }
 *
 * import org.agilewiki.jactor2.core.ActorBase;
 * import org.agilewiki.jactor2.core.processing.MessageProcessor;
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 *
 * //An actor which logs StatusUpdate events.
 * public class StatusLogger extends ActorBase implements StatusListener {
 *     //The logger.
 *     protected final Logger logger = LoggerFactory.getLogger(StatusLogger.class);
 *
 *     //Create a StatusLogger.
 *     public StatusLogger(final MessageProcessor _messageProcessor) throws Exception {
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
 * import org.agilewiki.jactor2.core.ActorBase;
 * import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
 * import org.agilewiki.jactor2.core.processing.MessageProcessor;
 * import org.agilewiki.jactor2.core.threading.ModuleContext;
 *
 * //An actor which prints status logger events.
 * public class StatusPrinter extends ActorBase implements StatusListener {
 *
 *     //Create an atomic StatusPrinter. (Atomic because the print mayblock the thread.)
 *     public StatusPrinter(final ModuleContext _moduleContext) throws Exception {
 *         MessageProcessor messageProcessor = new AtomicMessageProcessor(_moduleContext);
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