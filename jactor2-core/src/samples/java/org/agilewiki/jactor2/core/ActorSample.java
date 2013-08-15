package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class ActorSample implements Actor {
    private final MessageProcessor messageProcessor;

    ActorSample(final MessageProcessor _messageProcessor) {
        messageProcessor = _messageProcessor;
    }

    @Override
    public final MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }
}
