package org.agilewiki.jactor2.core.impl.requests;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.Request;
import org.agilewiki.jactor2.core.requests.SReq;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Test code.
 */
public class Blade11 extends IsolationBladeBase {

    public Blade11(final IsolationReactor mbox) throws Exception {
        super(mbox);
    }

    public SReq<String> hiSReq() {
        return new SReq<String>(getReactor()) {
            @Override
            protected String processSyncRequest(final Request _request) throws Exception {
                return "Hello world!";
            }
        };
    }
}
