package org.agilewiki.jactor2.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.*;

/**
 * Test code.
 */
public class SemaphoreTest extends TestCase implements Actor {
    Mailbox mailbox;

    @Override
    public Mailbox getMailbox() {
        return mailbox;
    }

    public void testI() throws Exception {
        final UtilMailboxFactory mailboxFactory = new UtilMailboxFactory();
        mailbox = mailboxFactory.createNonBlockingMailbox();
        final JASemaphore semaphore = new JASemaphore(
                mailboxFactory.createNonBlockingMailbox(), 1);
        semaphore.acquireReq().call();
        mailboxFactory.close();
    }

    public void testII() throws Exception {
        final UtilMailboxFactory mailboxFactory = new UtilMailboxFactory();
        mailbox = mailboxFactory.createNonBlockingMailbox();
        final JASemaphore semaphore = new JASemaphore(
                mailboxFactory.createNonBlockingMailbox(), 0);
        semaphore.release();
        semaphore.acquireReq().call();
        mailboxFactory.close();
    }

    private void delayedRelease(final JASemaphore semaphore,
                                final long delay,
                                final MailboxFactory mailboxFactory) throws Exception {
        new Event<SemaphoreTest>() {
            @Override
            public void processEvent(final SemaphoreTest actor)
                    throws Exception {
                new Delay(mailboxFactory).sleepReq(delay).send(getMailbox(),
                        new ResponseProcessor<Void>() {
                            @Override
                            public void processResponse(final Void response)
                                    throws Exception {
                                semaphore.release();
                            }
                        });
            }
        }.signal(this);
    }

    public void testIII() throws Exception {
        final UtilMailboxFactory mailboxFactory = new UtilMailboxFactory();
        mailbox = mailboxFactory.createNonBlockingMailbox();
        final JASemaphore semaphore = new JASemaphore(
                mailboxFactory.createNonBlockingMailbox(), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, mailboxFactory);
        semaphore.acquireReq().call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        mailboxFactory.close();
    }

    private Request<Boolean> acquireException(final JASemaphore semaphore,
                                              final Mailbox mailbox) {
        return new RequestBase<Boolean>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Boolean> responseProcessor)
                    throws Exception {
                mailbox.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        System.out.println(throwable);
                        responseProcessor.processResponse(true);
                    }
                });
                semaphore.acquireReq().send(mailbox,
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
        final UtilMailboxFactory mailboxFactory = new UtilMailboxFactory();
        mailbox = mailboxFactory.createNonBlockingMailbox();
        final JASemaphore semaphore = new JASemaphore(
                mailboxFactory.createNonBlockingMailbox(), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, mailboxFactory);
        final boolean result = acquireException(semaphore,
                mailboxFactory.createNonBlockingMailbox()).call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        assertTrue(result);
        mailboxFactory.close();
    }
}
