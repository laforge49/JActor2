package agilewiki.pactor.exceptions;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

public class ActorA {
    private final Mailbox mailbox;
    public final Request<Void> throwRequest;

    public ActorA(final Mailbox mbox) {
        this.mailbox = mbox;

        throwRequest = new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                throw new SecurityException("thrown on request");
            }
        };
    }
}
