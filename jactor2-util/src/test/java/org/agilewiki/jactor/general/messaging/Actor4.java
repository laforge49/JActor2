package org.agilewiki.jactor.general.messaging;

import org.agilewiki.jactor.api.*;

/**
 * Test code.
 */
public class Actor4 {
    private final Mailbox mailbox;

    public Actor4(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Request<Void> hi4(final Actor1 actor1) {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                actor1.hi1.send(mailbox, new ResponseProcessor<String>() {
                    @Override
                    public void processResponse(final String response)
                            throws Exception {
                        System.out.println(response);
                        responseProcessor.processResponse(null);
                    }
                });
            }
        };
    }
}
