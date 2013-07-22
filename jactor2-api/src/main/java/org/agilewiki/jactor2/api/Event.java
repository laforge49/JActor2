package org.agilewiki.jactor2.api;

/**
 * Event is typically subclassed to create requests that are targeted to a class
 * of actors or to an interface, rather than to a specific instance. The target class must however
 * implement the Actor interface.
 * <p/>
 * <pre>
 * public interface DudActor extends Actor {
 *     public String getDuddly();
 * }
 *
 * public class DuddlyReq extends Event&lt;String, DudActor&gt; {
 *     public void processRequest(final DudActor _targetActor, final ResponseProcessor&lt;String response&gt; _rp)
 *             throws Exception {
 *         _rp.processResponse(_targetActor.getDuddly());
 *     }
 * }
 * </pre>
 *
 * @param <TARGET_ACTOR_TYPE> The class of the actor that will be used when this Event is processed.
 */
public abstract class Event<TARGET_ACTOR_TYPE extends Actor> {

    /**
     * Passes this Request to the target Mailbox without a return address.
     * No result is passed back and if an exception is thrown while processing this Request,
     * that exception is simply logged as a warning.
     *
     * @param _targetActor The actor being operated on.
     */
    final public void signal(final TARGET_ACTOR_TYPE _targetActor) throws Exception {
        final EventMessage message = new EventMessage(_targetActor);
        message.event();
    }

    /**
     * The processRequest method will be invoked by the target Mailbox on its own thread
     * when this Request is received for processing.
     *
     * @param _targetActor The target actor for an Event.
     */
    abstract public void processEvent(final TARGET_ACTOR_TYPE _targetActor)
            throws Exception;

    final private class EventMessage implements Message {
        final TARGET_ACTOR_TYPE targetActor;

        EventMessage(final TARGET_ACTOR_TYPE _targetActor) {
            targetActor = _targetActor;
        }

        @Override
        public boolean isForeign() {
            return false;
        }

        @Override
        public boolean isResponsePending() {
            return false;
        }

        void event()
                throws Exception {
            targetActor.getMailbox().unbufferedAddMessages(this, false);
        }

        @Override
        public void close() throws Exception {
        }

        public void eval(final Mailbox _targetMailbox) {
            _targetMailbox.setExceptionHandler(null);
            _targetMailbox.setCurrentMessage(this);
            try {
                processEvent(targetActor);
            } catch (final Throwable t) {
                processThrowable(_targetMailbox, t);
            }
        }

        @Override
        public void processThrowable(final Mailbox _activeMailbox, final Throwable _t) {
            ExceptionHandler exceptionHandler = _activeMailbox.getExceptionHandler();
            if (exceptionHandler != null) {
                try {
                    exceptionHandler.processException(_t);
                } catch (final Throwable u) {
                    _activeMailbox.getLogger().error("Exception handler unable to process throwable "
                            + exceptionHandler.getClass().getName(), u);
                    _activeMailbox.getLogger().error("Thrown by exception handler and uncaught "
                            + exceptionHandler.getClass().getName(), _t);
                }
            }
        }
    }
}
