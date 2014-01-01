package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

public interface Activator extends Blade {
    void initialize(final NonBlockingReactor _reactor) throws Exception;

    AsyncRequest<Void> startAReq();
}
