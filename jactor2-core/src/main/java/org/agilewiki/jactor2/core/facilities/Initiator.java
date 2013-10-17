package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.Reactor;

public interface Initiator extends Blade {
    void initialize(final Reactor _reactor) throws Exception;

    AsyncRequest<Void> startAReq();
}
