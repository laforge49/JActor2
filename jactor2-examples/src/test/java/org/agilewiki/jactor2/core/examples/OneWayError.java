package org.agilewiki.jactor2.core.examples;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;

public class OneWayError extends NonBlockingBladeBase {
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public OneWayError() throws Exception {
    }

    static public void main(final String[] _args) throws Exception {
        new Plant();
        try {
            new OneRuntime().new OneWaySReq().signal();
            new OneWayError().new IndirectSReq().call();
            System.out.println("ok");
        } finally {
            Plant.close();
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
    /**
     * Create a non-blocking blade and a non-blocking reactor whose parent is the internal reactor of Plant.
     */
    public OneRuntime() throws Exception {
    }

    public class OneWaySReq extends SyncBladeRequest<Void> {
        @Override
        public Void processSyncRequest() throws RuntimeException {
            throw new RuntimeException();
        }
    }
}
