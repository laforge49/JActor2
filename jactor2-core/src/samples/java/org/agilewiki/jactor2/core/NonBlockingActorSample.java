package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.Facility;

public class NonBlockingActorSample extends NonBlockingActor {
    public NonBlockingActorSample(final Facility _facility) throws Exception {
        super(new NonBlockingMessageProcessor(_facility));
    }
}
