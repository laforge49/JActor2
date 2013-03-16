package agilewiki.pactor.exceptions;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

public class ActorA {
    private final Mailbox mailbox;

    public ActorA(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Request<Void> throwRequest() {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                throw new SecurityException("thrown on request");
            }
        };
    }
}
