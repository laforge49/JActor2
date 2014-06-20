package org.agilewiki.jactor2.core.impl.requests;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.requests.Request;
import org.agilewiki.jactor2.core.requests.SReq;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Test code.
 */
public class Blade3 extends IsolationBladeBase {

    public Blade3() throws Exception {
    }

    public SReq<Void> hi3SReq() {
        return new SReq<Void>(getReactor()) {
            @Override
            protected Void processSyncRequest(final Request _request) throws Exception {
                System.out.println("Hello world!");
                return null;
            }
        };
    }
}
