package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;

/**
 * Test code.
 */
public class Blade3 extends IsolationBladeBase {

    public Blade3(final Plant _plant) throws Exception {
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
