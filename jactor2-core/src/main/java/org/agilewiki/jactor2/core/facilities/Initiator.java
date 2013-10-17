package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

public interface Initiator extends Blade {
    AsyncRequest<Void> startAReq();
}
