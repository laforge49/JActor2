package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class Actor3 extends ActorBase {

    public Actor3(final ModuleContext _context) throws Exception {
        initialize(new IsolationMessageProcessor(_context));
    }

    public SyncRequest<Void> hi3SReq() {
        return new SyncRequest<Void>(getMessageProcessor()) {
            @Override
            public Void processSyncRequest() throws Exception {
                System.out.println("Hello world!");
                return null;
            }
        };
    }
}
