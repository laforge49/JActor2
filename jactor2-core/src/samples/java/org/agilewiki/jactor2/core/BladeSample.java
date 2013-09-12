package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.Reactor;

public class BladeSample implements Blade {
    private final Reactor reactor;

    BladeSample(final Reactor _reactor) {
        reactor = _reactor;
    }

    @Override
    public final Reactor getReactor() {
        return reactor;
    }
}
