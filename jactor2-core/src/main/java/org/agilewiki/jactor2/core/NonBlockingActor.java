package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

/**
 * An actor which does not perform long computations nor otherwise block the thread.
 */
public class NonBlockingActor extends ActorBase {

    /**
     * Create a non-blocking actor.
     *
     * @param _nonBlockingMessageProcessor A message processor for actors which process messages
     *                                     quickly and without blocking the thread.
     */
    public NonBlockingActor(final NonBlockingMessageProcessor _nonBlockingMessageProcessor)
            throws Exception {
        initialize(_nonBlockingMessageProcessor);
    }
}
