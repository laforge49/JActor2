package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Message wraps the user/application Request/Event which are queued in the
 * Actor's message processor's inbox. The lightweight thread associated with the Actor's message processor will process
 * the Message asynchronously.
 * <p>
 * Both Event and Request have private nested classes specific to their requirements.
 * </p>
 */

public interface Message extends AutoCloseable {

    /**
     * Returns true when the response is to be sent to a message processor created with a different
     * context.
     *
     * @return True when the response is to be sent to a message processor created with a different
     *         context.
     */
    boolean isForeign();

    /**
     * Returns true when a response is expected but has not yet been placed in the message.
     *
     * @return True when a response is expected but has not yet been placed in the message.
     */
    boolean isResponsePending();

    /**
     * Execute the Event.processEvent or Request.processRequest method
     * of the event/request held by the message. This method is always called on the
     * target message processor's own thread.
     */
    void eval();

    /**
     * Process the throwable on the current thread in the context of the active message processor.
     *
     * @param _activeMessageProcessor The message processor providing the context for processing the throwable.
     * @param _t                      The throwable to be processed.
     */
    void processThrowable(final MessageProcessor _activeMessageProcessor, final Throwable _t);
}
