package agilewiki.pactor.semaphore;

import junit.framework.TestCase;

import org.agilewiki.pactor.Delay;
import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;
import org.agilewiki.pactor.Semaphore;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Semaphore semaphore = new Semaphore(
                mailboxFactory.createMailbox(), 1);
        semaphore.acquire().pend();
        mailboxFactory.shutdown();
    }

    public void testII() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Semaphore semaphore = new Semaphore(
                mailboxFactory.createMailbox(), 0);
        semaphore.release().send();
        semaphore.acquire().pend();
        mailboxFactory.shutdown();
    }

    private Request<Void> delayedRelease(final Semaphore semaphore,
            final long delay, final MailboxFactory mailboxFactory) {
        return new Request<Void>(mailboxFactory.createMailbox()) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                new Delay(mailboxFactory).sleep(delay).reply(getMailbox(),
                        new ResponseProcessor<Void>() {
                            @Override
                            public void processResponse(final Void response)
                                    throws Exception {
                                semaphore.release().send();
                                responseProcessor.processResponse(null);
                            }
                        });
            }
        };
    }

    public void testIII() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Semaphore semaphore = new Semaphore(
                mailboxFactory.createMailbox(), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, mailboxFactory).send();
        semaphore.acquire().pend();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        mailboxFactory.shutdown();
    }

    private Request<Boolean> acquireException(final Semaphore semaphore,
            final Mailbox mailbox) {
        return new Request<Boolean>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Boolean> responseProcessor)
                    throws Exception {
                mailbox.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        responseProcessor.processResponse(true);
                    }
                });
                semaphore.acquire().reply(mailbox,
                        new ResponseProcessor<Void>() {
                            @Override
                            public void processResponse(final Void response)
                                    throws Exception {
                                throw new SecurityException(
                                        "thrown after acquire");
                            }
                        });
            }
        };
    }

    public void testIV() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Semaphore semaphore = new Semaphore(
                mailboxFactory.createMailbox(), 0);
        final long d = 1000;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, mailboxFactory).send();
        final boolean result = acquireException(semaphore,
                mailboxFactory.createMailbox()).pend();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        assertTrue(result);
        mailboxFactory.shutdown();
    }
}
