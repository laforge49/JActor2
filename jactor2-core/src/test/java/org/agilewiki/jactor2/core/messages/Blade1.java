package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Blade1 extends BladeBase {

    public Blade1(final Reactor mbox) throws Exception {
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
