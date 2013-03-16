package agilewiki.pactor.parallel;

import junit.framework.TestCase;
import org.agilewiki.pactor.*;

/**
 * Test code.
 */
public class ParallelTest extends TestCase {
    private final static int loads = 10;
    private final static long delay = 200;

    private Mailbox mailbox;
    private MailboxFactory mailboxFactory;

    public void test() throws Throwable {
        mailboxFactory = new MailboxFactory();
        mailbox = mailboxFactory.createMailbox();
        long t0 = System.currentTimeMillis();
        start().pend();
        long t1 = System.currentTimeMillis();
        assertTrue((t1 - t0) < delay + delay / 2);
        mailboxFactory.shutdown();
    }

    private Request<Void> start() {
        return new Request<Void>(mailbox) {
            @Override
            public void processRequest(ResponseProcessor<Void> responseProcessor)
                    throws Throwable {
                ResponseCounter<Void> responseCounter =
                        new ResponseCounter<Void>(loads, responseProcessor, null);
                int i = 0;
                while (i < loads) {
                    Delay delay = new Delay(mailboxFactory);
                    delay.sleep(ParallelTest.delay).reply(mailbox, responseCounter);
                    i += 1;
                }
            }
        };
    }
}
