package agilewiki.pactor.messaging;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Test code.
 */
public class Actor3 {
    private final Mailbox mailbox;
    public final Request<Void> hi3;

    public Actor3(final Mailbox mbox) {
        this.mailbox = mbox;

        hi3 = new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                System.out.println("Hello world!");
                responseProcessor.processResponse(null);
            }
        };
    }
}
