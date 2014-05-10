package org.agilewiki.jactor2.core.impl.plant;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.impl.JActorStTestPlantConfiguration;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class GwtTestSyncTest extends BaseGWTTestCase {
    public void testa() throws Exception {
        final JActorStTestPlantConfiguration config = new JActorStTestPlantConfiguration();
        new Plant(new JActorStTestPlantConfiguration());
        try {
            final Sync1 sync1 = new Sync1();
            sync1.startSReq().signal();
        } finally {
            Plant.close();
        }
    }
}

class Sync1 extends NonBlockingBladeBase {
    Sync1() {
        super(new NonBlockingReactor());
    }

    SyncRequest<Void> startSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws Exception {
                System.out.println("Hi");
                return null;
            }
        };
    }
}
