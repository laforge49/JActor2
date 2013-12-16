package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

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
            protected String processSyncRequest() throws Exception {
                return "Hello world!";
            }
        };
    }
}
