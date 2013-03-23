package agilewiki.pactor.exceptions;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

public class ActorA {
    private final Mailbox mailbox;

    public ActorA(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public RequestBase<Void> throwRequest() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                throw new SecurityException("thrown on request");
            }
        };
    }
}
