package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

public class NonBlockingActor extends ActorBase {
    public NonBlockingActor(final NonBlockingMessageProcessor _nonBlockingMessageProcessor) throws Exception {
        initialize(_nonBlockingMessageProcessor);
    }
}
