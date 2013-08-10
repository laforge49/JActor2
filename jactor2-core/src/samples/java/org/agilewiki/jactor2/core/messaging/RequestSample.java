package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.mailbox.NonBlockingMailbox;

public class RequestSample {

    public static void main(String[] args) throws Exception {

        //A context with one thread.
        final JAContext jaContext = new JAContext(1);

        SampleActor2 actorA = new SampleActor2(new NonBlockingMailbox(jaContext));

        actorA.updateReq(1).signal();

    }

}

class SampleActor2 extends ActorBase {

    private int state = 0;

    SampleActor2(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox);
    }

    Request<Integer> updateReq(final int _newState) {
        return new Request<Integer>(getMailbox()) {
            @Override
            public void processRequest(Transport<Integer> _transport) throws Exception {
                int oldState = state;
                state = _newState;
                _transport.processResponse(oldState);
            }
        };
    }

}