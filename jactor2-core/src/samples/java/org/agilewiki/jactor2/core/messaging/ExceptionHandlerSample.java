package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.mailbox.NonBlockingMailbox;

public class ExceptionHandlerSample {

    public static void main(final String[] _args) throws Exception {

        //A context with two threads.
        final JAContext jaContext = new JAContext(2);

        try {

            ExceptionActor exceptionActor = new ExceptionActor(new NonBlockingMailbox(jaContext));

            try {
                exceptionActor.exceptionReq().call();
                System.out.println("can't get here");
            } catch (IllegalStateException ise) {
                System.out.println("got first IllegalStateException, as expected");
            }

        } finally {
            //shutdown the context
            jaContext.close();
        }
    }
}

class ExceptionActor extends ActorBase {

    ExceptionActor(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
    }

    Request<Void> exceptionReq() {
        return new Request<Void>(getMailbox()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                throw new IllegalStateException();
            }
        };
    }
}