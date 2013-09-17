package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Message wraps the user/application AsyncRequest/Event which are queued in the
 * Blade's targetReactor's inbox. The lightweight thread associated with the Blade's targetReactor will process
 * the Message asynchronously.
 * <p>
 * Both Event and AsyncRequest have private nested classes specific to their requirements.
 * </p>
 */

public interface Message extends AutoCloseable {

    /**
     * Returns true when the response is to be sent to a targetReactor created with a different
     * facility.
     *
     * @return True when the response is to be sent to a targetReactor created with a different
     *         facility.
     */
    boolean isForeign();

    /**
     * Returns true when a response is expected but has not yet been placed in the message.
     *
     * @return True when a response is expected but has not yet been placed in the message.
     */
    boolean isResponsePending();

    /**
     * Execute the Event.processEvent or AsyncRequest.processAsyncRequest method
     * of the event/request held by the message. This method is always called on the
     * target targetReactor's own thread.
     */
    void eval();

    /**
     * Process the throwable on the current thread in the facility of the active targetReactor.
     *
     * @param _activeReactor The targetReactor providing the facility for processing the throwable.
     * @param _e             The exception to be processed.
     */
    void processException(final Reactor _activeReactor, final Exception _e);
}
