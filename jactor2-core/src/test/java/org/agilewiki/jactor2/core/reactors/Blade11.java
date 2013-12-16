package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.ThreadBoundBladeBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;

/**
 * Test code.
 */
public class Blade11 extends ThreadBoundBladeBase {

    public Blade11(final ThreadBoundReactor mbox) throws Exception {
        initialize(mbox);
    }

    public SyncRequest<String> hiSReq() {
        return new SyncBladeRequest<String>() {
            @Override
            protected String processSyncRequest() throws Exception {
                return "Hello world!";
            }
        };
    }
}
