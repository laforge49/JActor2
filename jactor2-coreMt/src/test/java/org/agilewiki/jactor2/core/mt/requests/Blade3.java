package org.agilewiki.jactor2.core.mt.requests;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * Test code.
 */
public class Blade3 extends IsolationBladeBase {

    public Blade3() throws Exception {
    }

    public SyncRequest<Void> hi3SReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws Exception {
                System.out.println("Hello world!");
                return null;
            }
        };
    }
}
