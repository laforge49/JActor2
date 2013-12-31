package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * Test code.
 */
public class Blade3 extends IsolationBladeBase {

    public Blade3(final BasicPlant _plant) throws Exception {
        initialize(new IsolationReactor());
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
