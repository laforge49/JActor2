package agilewiki.pactor.basics;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Test code.
 */
public class Actor2 {
    private final Mailbox mailbox;

    public Actor2(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public Request<String> hi2(final Actor1 actor1) {
        return new Request<String>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<String> responseProcessor) throws Exception {
                actor1.hi1().send(mailbox, responseProcessor);
            }
        };
    }
}
