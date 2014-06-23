package org.agilewiki.jactor2.core.impl.requests;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.requests.Request;
import org.agilewiki.jactor2.core.requests.SOp;

/**
 * Test code.
 */
public class Blade11 extends IsolationBladeBase {

    public Blade11(final IsolationReactor mbox) throws Exception {
        super(mbox);
    }

    public SOp<String> hiSReq() {
        return new SOp<String>(getReactor()) {
            @Override
            protected String processSyncRequest(final Request _request) throws Exception {
                return "Hello world!";
            }
        };
    }
}
