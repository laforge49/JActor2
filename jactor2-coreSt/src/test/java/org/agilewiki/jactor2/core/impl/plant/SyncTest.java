package org.agilewiki.jactor2.core.impl.plant;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.impl.TestPlantConfiguration;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class SyncTest extends TestCase {
    public void testa() throws Exception {
        TestPlantConfiguration config = new TestPlantConfiguration();
        new Plant(new TestPlantConfiguration());
        try {
            Sync1 sync1 = new Sync1();
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
                System.out.println("Hi1");
                return null;
            }
        };
    }
}
