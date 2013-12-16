package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public interface Activator extends Blade {
    void initialize(final NonBlockingReactor _reactor) throws Exception;

    AsyncRequest<Void> startAReq();
}
