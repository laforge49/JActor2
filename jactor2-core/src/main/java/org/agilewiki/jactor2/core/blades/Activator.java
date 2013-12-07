package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public interface Activator extends Blade {
    void initialize(final Reactor _reactor) throws Exception;

    AsyncRequest<Void> startAReq();
}
