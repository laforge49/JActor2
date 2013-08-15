package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class ActorBaseSample extends ActorBase {
    public ActorBaseSample(final MessageProcessor _MessageProcessor) throws Exception {
        initialize(_MessageProcessor);
    }
}
