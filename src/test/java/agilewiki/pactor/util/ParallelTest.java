package agilewiki.pactor.util;

import junit.framework.TestCase;
import org.agilewiki.pactor.*;
import org.agilewiki.pactor.util.Delay;
import org.agilewiki.pactor.util.ResponseCounter;

/**
 * Test code.
 */
public class ParallelTest extends TestCase {
    private static final int LOADS = 10;
    private static final long DELAY = 200;

    private Mailbox mailbox;
    private MailboxFactory mailboxFactory;

    public void test() throws Exception {
        mailboxFactory = new MailboxFactory();
        mailbox = mailboxFactory.createMailbox();
        final long t0 = System.currentTimeMillis();
        start().pend();
        final long t1 = System.currentTimeMillis();
        assertTrue((t1 - t0) < DELAY + DELAY / 2);
        mailboxFactory.shutdown();
    }

    private Request<Void> start() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                final ResponseCounter<Void> responseCounter = new ResponseCounter<Void>(
                        LOADS, responseProcessor, null);
                int i = 0;
                while (i < LOADS) {
                    final Delay dly = new Delay(mailboxFactory);
                    dly.sleep(ParallelTest.DELAY).reply(mailbox,
                            responseCounter);
                    i += 1;
                }
            }
        };
    }
}
