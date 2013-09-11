package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class NonBlockingActorSample extends NonBlockingActor {
    public NonBlockingActorSample(final ModuleContext _moduleContext) throws Exception {
        super(new NonBlockingMessageProcessor(_moduleContext));
    }
}
