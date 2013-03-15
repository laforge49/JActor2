package agilewiki.pactor.exceptions;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

public class ActorB {
    private final Mailbox mailbox;

    public ActorB(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public Request<Void> throwRequest(final ActorA actorA) {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<Void> responseProcessor) throws Exception {
                actorA.throwRequest().reply(mailbox, responseProcessor);
            }
        };
    }
}
