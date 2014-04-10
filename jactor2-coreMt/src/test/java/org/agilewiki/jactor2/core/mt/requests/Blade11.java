package org.agilewiki.jactor2.core.mt.requests;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Test code.
 */
public class Blade11 extends IsolationBladeBase {

    public Blade11(final IsolationReactor mbox) throws Exception {
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
