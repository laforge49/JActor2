package agilewiki.pactor.messaging;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Test code.
 */
public class Actor2 {
    private final Mailbox mailbox;

    public Actor2(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Request<String> hi2(final Actor1 actor1) {
        return new RequestBase<String>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<String> responseProcessor)
                    throws Exception {
                actor1.hi1.reply(mailbox, responseProcessor);
            }
        };
    }
}
