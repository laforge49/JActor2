package agilewiki.pactor.messaging;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Test code.
 */
public class Actor1 {
    private final Mailbox mailbox;

    public Actor1(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public RequestBase<String> hi1() {
        return new RequestBase<String>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<String> responseProcessor)
                    throws Exception {
                responseProcessor.processResponse("Hello world!");
            }
        };
    }
}
