package agilewiki.pactor.messaging;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Test code.
 */
public class Actor3 {
    private final Mailbox mailbox;

    public Actor3(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public Request<Void> hi3() {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<Void> responseProcessor) throws Exception {
                System.out.println("Hello world!");
                responseProcessor.processResponse(null);
            }
        };
    }
}
