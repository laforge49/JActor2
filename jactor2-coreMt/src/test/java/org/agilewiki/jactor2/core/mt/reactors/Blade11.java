package org.agilewiki.jactor2.core.mt.reactors;

import org.agilewiki.jactor2.core.blades.ThreadBoundBladeBase;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Test code.
 */
public class Blade11 extends ThreadBoundBladeBase {

    public Blade11(final ThreadBoundReactor mbox) throws Exception {
        super(mbox);
    }

    public SyncRequest<String> hiSReq() {
        return new SyncBladeRequest<String>() {
            @Override
            public String processSyncRequest() throws Exception {
                return "Hello world!";
            }
        };
    }
}
