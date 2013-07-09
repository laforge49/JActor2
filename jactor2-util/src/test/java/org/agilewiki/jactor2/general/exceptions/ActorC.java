package org.agilewiki.jactor2.general.exceptions;

import org.agilewiki.jactor2.api.*;

public class ActorC {
    private final Mailbox mailbox;
    public final Request<String> throwRequest;

    public ActorC(final Mailbox mbox) {
        this.mailbox = mbox;

        throwRequest = new RequestBase<String>(mailbox) {
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
                throw new SecurityException("thrown on request");
            }
        };
    }
}
