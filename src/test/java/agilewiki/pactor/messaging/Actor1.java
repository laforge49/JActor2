package agilewiki.pactor.messaging;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Test code.
 */
public class Actor1 {
    private final Mailbox mailbox;

    public Actor1(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public Request<String> hi1() {
        return new Request<String>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<String> responseProcessor) throws Throwable {
                responseProcessor.processResponse("Hello world!");
            }
        };
    }
}
