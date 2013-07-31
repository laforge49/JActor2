package org.agilewiki.jactor2.core;

/**
 * An Event instance is used to pass one-way messages to any number of Actor objects.
 * Event messages are unbuffered and are sent immediately.
 *
 * @param <TARGET_ACTOR_TYPE> The class of the actor that will be targeted when this Event is processed.
 */
public abstract class Event<TARGET_ACTOR_TYPE extends Actor> {

    /**
     * Passes an event message immediately to the target Mailbox for subsequent processing
     * by the thread of the mailbox.
     * No result is passed back and if an exception is thrown while processing the event,
     * that exception is simply logged as a warning.
     *
     * @param _targetActor The actor to be operated on.
     */
    final public void signal(final TARGET_ACTOR_TYPE _targetActor) throws Exception {
        final EventMessage message = new EventMessage(_targetActor);
        _targetActor.getMailbox().unbufferedAddMessages(message, false);
    }

    /**
     * The processEvent method will be invoked by the target Mailbox on its own thread
     * when this event is processed.
     *
     * @param _targetActor The actor to be operated on.
     */
    abstract public void processEvent(final TARGET_ACTOR_TYPE _targetActor)
            throws Exception;

    /**
     * The message subclass used to pass events.
     */
    final private class EventMessage implements Message {

        /**
         * The actor to be operated on.
         */
        final TARGET_ACTOR_TYPE targetActor;

        /**
         * Create an EventMessage.
         *
         * @param _targetActor The actor to be operated on.
         */
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

        @Override
        public void close() throws Exception {
        }

        /**
         * Process the event with the target mailbox thread.
         *
         * @param _targetMailbox The mailbox of the target actor.
         */
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
