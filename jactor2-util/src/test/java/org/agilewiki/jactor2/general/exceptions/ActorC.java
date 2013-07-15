package org.agilewiki.jactor2.general.exceptions;

import org.agilewiki.jactor2.api.*;

public class ActorC {
    private final Mailbox mailbox;
    public final BoundRequest<String> throwBoundRequest;

    public ActorC(final Mailbox mbox) {
        this.mailbox = mbox;

        throwBoundRequest = new BoundRequestBase<String>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<String> responseProcessor)
                    throws Exception {
                mailbox.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        responseProcessor.processResponse(throwable.toString());
                    }
                });
                throw new SecurityException("thrown on boundRequest");
            }
        };
    }
}
