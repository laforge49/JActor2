package org.agilewiki.core.exceptions;

import org.agilewiki.jactor2.core.ExceptionHandler;
import org.agilewiki.jactor2.core.Mailbox;
import org.agilewiki.jactor2.core.Request;
import org.agilewiki.jactor2.core.Transport;

public class ActorC {
    private final Mailbox mailbox;
    public final Request<String> throwRequest;

    public ActorC(final Mailbox mbox) {
        this.mailbox = mbox;

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
                throw new SecurityException("thrown on request");
            }
        };
    }
}
