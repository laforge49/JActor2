package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class ActorBaseSample extends ActorBase {
    public ActorBaseSample(final MessageProcessor _messageProcessor) throws Exception {
        initialize(_messageProcessor);
    }
}
