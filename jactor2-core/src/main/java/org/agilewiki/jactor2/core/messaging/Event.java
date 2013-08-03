package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.Actor;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.mailbox.MailboxBase;

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
        ((MailboxBase) _targetActor.getMailbox()).unbufferedAddMessages(message, false);
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
            MailboxBase targetMailbox = (MailboxBase) _targetMailbox;
            targetMailbox.setExceptionHandler(null);
            targetMailbox.setCurrentMessage(this);
            try {
                processEvent(targetActor);
            } catch (final Throwable t) {
                processThrowable(targetMailbox, t);
            }
        }

        @Override
        public void processThrowable(final Mailbox _activeMailbox, final Throwable _t) {
            MailboxBase activeMailbox = (MailboxBase) _activeMailbox;
            ExceptionHandler exceptionHandler = activeMailbox.getExceptionHandler();
            if (exceptionHandler != null) {
                try {
                    exceptionHandler.processException(_t);
                } catch (final Throwable u) {
                    activeMailbox.getLogger().error("Exception handler unable to process throwable "
                            + exceptionHandler.getClass().getName(), u);
                    activeMailbox.getLogger().error("Thrown by exception handler and uncaught "
                            + exceptionHandler.getClass().getName(), _t);
                }
            }
        }
    }
}
