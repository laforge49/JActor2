package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Actor4 {
    private final MessageProcessor messageProcessor;

    public Actor4(final MessageProcessor mbox) {
        this.messageProcessor = mbox;
    }

    public SyncRequest<Void> hi4SReq() {
        return new SyncRequest<Void>(messageProcessor) {
            @Override
            public Void processSyncRequest()
                    throws Exception {
                new Actor1(messageProcessor).hiSReq().local(messageProcessor);
                System.out.println(response);
                return null;
            }
        };
    }
}
