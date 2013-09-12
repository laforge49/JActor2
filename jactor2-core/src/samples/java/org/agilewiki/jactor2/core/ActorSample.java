package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.Reactor;

public class ActorSample implements Actor {
    private final Reactor reactor;

    ActorSample(final Reactor _reactor) {
        reactor = _reactor;
    }

    @Override
    public final Reactor getReactor() {
        return reactor;
    }
}
