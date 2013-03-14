package agilewiki.pactor.basics;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Test code.
 */
public class Actor4 {
    private final Mailbox mailbox;

    public Actor4(Mailbox mailbox) {
        this.mailbox = mailbox;
    }

    public Request<Void> hi4(final Actor1 actor1) {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<Void> responseProcessor) throws Exception {
                actor1.hi1().send(mailbox, new ResponseProcessor<String>() {
                    @Override
                    public void processResponse(String response) throws Exception {
                        System.out.println(response);
                    }
                });
                responseProcessor.processResponse(null);
            }
        };
    }
}
