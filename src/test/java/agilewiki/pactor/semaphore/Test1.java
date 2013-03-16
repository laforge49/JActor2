package agilewiki.pactor.semaphore;

import junit.framework.TestCase;
import org.agilewiki.pactor.*;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Semaphore semaphore = new Semaphore(mailboxFactory.createMailbox(), 1);
        semaphore.acquire().pend();
        mailboxFactory.shutdown();
    }

    public void testII() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Semaphore semaphore = new Semaphore(mailboxFactory.createMailbox(), 0);
        semaphore.release().send();
        semaphore.acquire().pend();
        mailboxFactory.shutdown();
    }

    private Request<Void> delayedRelease(
            final Semaphore semaphore,
            final long delay,
            final MailboxFactory mailboxFactory) {
        return new Request<Void>(mailboxFactory.createMailbox()) {
            @Override
            public void processRequest(final ResponseProcessor<Void> responseProcessor) throws Throwable {
                new Delay(mailboxFactory).sleep(delay).reply(getMailbox(), new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(Void response) throws Throwable {
                        semaphore.release().send();
                        responseProcessor.processResponse(null);
                    }
                });
            }
        };
    }

    public void testIII() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Semaphore semaphore = new Semaphore(mailboxFactory.createMailbox(), 0);
        long d = 100;
        long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, mailboxFactory).send();
        semaphore.acquire().pend();
        long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        mailboxFactory.shutdown();
    }

    private Request<Boolean> acquireException(final Semaphore semaphore, final Mailbox mailbox) {
        return new Request<Boolean>(mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<Boolean> responseProcessor) throws Throwable {
                mailbox.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        System.out.println(throwable);
                        responseProcessor.processResponse(true);
                    }
                });
                semaphore.acquire().reply(mailbox, new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(Void response) throws Throwable {
                        throw new SecurityException("thrown after acquire");
                    }
                });
            }
        };
    }

    public void testIV() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Semaphore semaphore = new Semaphore(mailboxFactory.createMailbox(), 0);
        long d = 100;
        long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, mailboxFactory).send();
        boolean result = acquireException(semaphore, mailboxFactory.createMailbox()).pend();
        long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        assertTrue(result);
        mailboxFactory.shutdown();
    }
}
