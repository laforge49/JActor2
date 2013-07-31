package org.agilewiki.core.exceptions;

import org.agilewiki.jactor2.core.ExceptionHandler;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.messaging.Transport;

public class ActorD {
    private final Mailbox mailbox;
    public final Request<String> throwRequest;

    public ActorD(final Mailbox mbox) {
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
                Dd dd = new Dd(mailbox.getContext().createAtomicMailbox());
                dd.doSomethin.send(mailbox, new ResponseProcessor<Void>() {
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

class Dd {
    private final Mailbox mailbox;
    final Request<Void> doSomethin;

    public Dd(final Mailbox mbox) {
        mailbox = mbox;

        doSomethin = new Request<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                responseProcessor.processResponse(null);
            }
        };
    }
}