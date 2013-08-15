package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Message wraps the user/application Request/Event which are queued in the
 * Actor's processing. The lightweight thread associated with the Actor's processing will process
 * the Message asynchronously.
 * <p>
 * Both Event and Request have private nested classes specific to their requirements.
 * </p>
 */

public interface Message extends AutoCloseable {

    /**
     * Returns true when the response is to be sent to a processing created from a different
     * processing factory.
     *
     * @return True when the response is to be sent to a processing created from a different
     *         processing factory.
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
     * target processing's own thread.
     *
     * @param _targetMessageProcessor The processing whose thread is to evaluate the event/request.
     */
    void eval(final MessageProcessor _targetMessageProcessor);

    /**
     * Process the throwable on the current thread in the context of the active processing.
     *
     * @param _activeMessageProcessor The processing providing the context for processing the throwable.
     * @param _t                      The throwable to be processed.
     */
    void processThrowable(final MessageProcessor _activeMessageProcessor, final Throwable _t);
}
