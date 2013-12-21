package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

class Hanger extends NonBlockingBladeBase {
    Hanger(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    SyncRequest<Void> looperSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws Exception {
                while (true) {}
            }
        };
    }

    SyncRequest<Void> sleeperSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            public Void processSyncRequest() throws Exception {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ie) {
                    throw ie;
                }
                return null;
            }
        };
    }
}
