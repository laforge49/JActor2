package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.Reactor;

/**
 * Test code.
 */
public class Actor1 extends ActorBase {

    public Actor1(final Reactor mbox) throws Exception {
        initialize(mbox);
    }

    public SyncRequest<String> hiSReq() {
        return new SyncRequest<String>(getReactor()) {
            @Override
            public String processSyncRequest() throws Exception {
                return "Hello world!";
            }
        };
    }
}
