package agilewiki.pactor.parallel;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Test code.
 */
public class Load {
    private final Mailbox mailbox;

    public Load(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public Request<Void> sleep(final long delay) {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<Void> responseProcessor) throws Throwable {
                Thread.sleep(delay);
                responseProcessor.processResponse(null);
            }
        };
    }
}
