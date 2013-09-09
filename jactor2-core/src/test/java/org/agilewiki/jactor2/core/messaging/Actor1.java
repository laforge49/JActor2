package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Actor1 extends ActorBase {

    public Actor1(final MessageProcessor mbox) throws Exception {
        initialize(mbox);
    }

    public SyncRequest<String> hiSReq() {
        return new SyncRequest<String>(getMessageProcessor()) {
            @Override
            public String processSyncRequest() throws Exception {
                return "Hello world!";
            }
        };
    }
}
