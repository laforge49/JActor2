package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.reactors.Reactor;

public class DefaultRecovery implements Recovery {
    @Override
    public long getThreadInterruptMilliseconds(final Reactor _reactor) {
        return 3000;
    }

    @Override
    public void hungThread(Reactor _reactor) {
        _reactor.getFacility().getPlant().forceExit();
    }
}
