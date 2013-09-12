package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Actor3 extends ActorBase {

    public Actor3(final Facility _facility) throws Exception {
        initialize(new IsolationReactor(_facility));
    }

    public SyncRequest<Void> hi3SReq() {
        return new SyncRequest<Void>(getReactor()) {
            @Override
            public Void processSyncRequest() throws Exception {
                System.out.println("Hello world!");
                return null;
            }
        };
    }
}
