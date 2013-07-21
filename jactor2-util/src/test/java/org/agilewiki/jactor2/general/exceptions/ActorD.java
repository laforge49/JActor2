package org.agilewiki.jactor2.general.exceptions;

import org.agilewiki.jactor2.api.*;

public class ActorD {
    private final Mailbox mailbox;
    final Request<Void> doSomethin;
    public final Request<String> throwRequest;

    public ActorD(final Mailbox mbox) {
        this.mailbox = mbox;

        doSomethin = new Request<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                responseProcessor.processResponse(null);
            }
        };

        throwRequest = new Request<String>(mailbox) {
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
                doSomethin.send(mailbox, new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(final Void response)
                            throws Exception {
                        throw new SecurityException("thrown on request");
                    }
                });
            }
        };
    }
}
