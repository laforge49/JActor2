package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.reactors.Reactor;

public interface Recovery {
    long getThreadInterruptMilliseconds(Reactor _reactor);
    void hungThread(Reactor _reactor);
}
