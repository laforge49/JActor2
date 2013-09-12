package org.agilewiki.jactor2.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Blade;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messaging.Event;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.misc.Delay;
import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class SemaphoreTest extends TestCase implements Blade {
    Reactor reactor;

    @Override
    public Reactor getReactor() {
        return reactor;
    }

    public void testI() throws Exception {
        final Facility facility = new Facility();
        reactor = new NonBlockingReactor(facility);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingReactor(facility), 1);
        semaphore.acquireReq().call();
        facility.close();
    }

    public void testII() throws Exception {
        final Facility facility = new Facility();
        reactor = new NonBlockingReactor(facility);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingReactor(facility), 0);
        semaphore.release();
        semaphore.acquireReq().call();
        facility.close();
    }

    private void delayedRelease(final JASemaphore semaphore,
                                final long delay,
                                final Facility facility) throws Exception {
        new Event<SemaphoreTest>() {
            @Override
            public void processEvent(final SemaphoreTest _targetBlade)
                    throws Exception {
                new Delay(facility).sleepSReq(delay).send(getReactor(),
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(final Void response)
                                    throws Exception {
                                semaphore.release();
                            }
                        });
            }
        }.signal(this);
    }

    public void testIII() throws Exception {
        final Facility facility = new Facility();
        reactor = new NonBlockingReactor(facility);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingReactor(facility), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, facility);
        semaphore.acquireReq().call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        facility.close();
    }

    private AsyncRequest<Boolean> acquireException(final JASemaphore semaphore,
                                                   final Reactor reactor) {
        return new AsyncRequest<Boolean>(reactor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                setExceptionHandler(new ExceptionHandler<Boolean>() {
                    @Override
                    public Boolean processException(final Exception exception)
                            throws Exception {
                        System.out.println(exception);
                        return true;
                    }
                });
                semaphore.acquireReq().send(messageProcessor,
                        new AsyncResponseProcessor<Void>() {
                            @Override
                            public void processAsyncResponse(final Void response)
                                    throws Exception {
                                throw new SecurityException(
                                        "thrown after acquire");
                            }
                        });
            }
        };
    }

    public void testIV() throws Exception {
        final Facility facility = new Facility();
        reactor = new NonBlockingReactor(facility);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingReactor(facility), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, facility);
        final boolean result = acquireException(semaphore,
                new NonBlockingReactor(facility)).call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        assertTrue(result);
        facility.close();
    }
}
