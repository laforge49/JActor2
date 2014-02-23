package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;

public class OneWayError extends NonBlockingBladeBase {
    static public void main(final String[] _args) throws Exception {
        final Plant plant = new Plant();
        try {
            new OneRuntime().new OneWaySReq().signal();
            new OneWayError().new IndirectSReq().call();
            System.out.println("ok");
        } finally {
            plant.close();
        }
    }

    public class IndirectSReq extends AsyncBladeRequest<Void> {
        @Override
        public void processAsyncRequest() throws Exception {
            send(new OneRuntime().new OneWaySReq(), null);
            processAsyncResponse(null);
        }
    }
}

class OneRuntime extends NonBlockingBladeBase {
    public class OneWaySReq extends SyncBladeRequest<Void> {
        @Override
        public Void processSyncRequest() throws RuntimeException {
            throw new RuntimeException();
        }
    }
}
