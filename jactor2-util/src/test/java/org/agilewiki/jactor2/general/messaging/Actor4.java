package org.agilewiki.jactor2.general.messaging;

import org.agilewiki.jactor2.api.*;

/**
 * Test code.
 */
public class Actor4 {
    private final Mailbox mailbox;

    public Actor4(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public BoundRequest<Void> hi4(final Actor1 actor1) {
        return new BoundRequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
               new Hi1().send(mailbox, actor1, new ResponseProcessor<String>() {
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
