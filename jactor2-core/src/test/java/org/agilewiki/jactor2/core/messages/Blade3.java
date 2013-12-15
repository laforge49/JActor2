package org.agilewiki.jactor2.core.messages;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * Test code.
 */
public class Blade3 extends BladeBase {

    public Blade3(final Plant _plant) throws Exception {
        initialize(new IsolationReactor(_plant));
    }

    public SyncRequest<Void> hi3SReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                System.out.println("Hello world!");
                return null;
            }
        };
    }
}
