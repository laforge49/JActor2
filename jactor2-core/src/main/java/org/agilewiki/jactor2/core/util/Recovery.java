package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;

public class Recovery {

    public long getReactorPollMillis() {
        return 1000;
    }

    public long messageTimeoutMillis() {
        return 3000;
    }

    public void messageTimeout(ReactorBase _reactor) throws Exception {
        _reactor.close();
    }

    public long getThreadInterruptMillis(final Reactor _reactor) {
        return 3000;
    }

    public void hungThread(ReactorBase _reactor) {
        _reactor.getFacility().getPlant().forceExit();
    }
}
