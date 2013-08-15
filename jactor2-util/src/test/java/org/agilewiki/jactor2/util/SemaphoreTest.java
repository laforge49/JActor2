package org.agilewiki.jactor2.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Actor;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.*;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

/**
 * Test code.
 */
public class SemaphoreTest extends TestCase implements Actor {
    MessageProcessor messageProcessor;

    @Override
    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    public void testI() throws Exception {
        final JAContext jaContext = new JAContext();
        messageProcessor = new NonBlockingMessageProcessor(jaContext);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingMessageProcessor(jaContext), 1);
        semaphore.acquireReq().call();
        jaContext.close();
    }

    public void testII() throws Exception {
        final JAContext jaContext = new JAContext();
        messageProcessor = new NonBlockingMessageProcessor(jaContext);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingMessageProcessor(jaContext), 0);
        semaphore.release();
        semaphore.acquireReq().call();
        jaContext.close();
    }

    private void delayedRelease(final JASemaphore semaphore,
                                final long delay,
                                final JAContext jaContext) throws Exception {
        new Event<SemaphoreTest>() {
            @Override
            public void processEvent(final SemaphoreTest actor)
                    throws Exception {
                new Delay(jaContext).sleepReq(delay).send(getMessageProcessor(),
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
        final JAContext jaContext = new JAContext();
        messageProcessor = new NonBlockingMessageProcessor(jaContext);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingMessageProcessor(jaContext), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, jaContext);
        semaphore.acquireReq().call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        jaContext.close();
    }

    private Request<Boolean> acquireException(final JASemaphore semaphore,
                                              final MessageProcessor messageProcessor) {
        return new Request<Boolean>(messageProcessor) {
            @Override
            public void processRequest(
                    final Transport<Boolean> responseProcessor)
                    throws Exception {
                messageProcessor.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        System.out.println(throwable);
                        responseProcessor.processResponse(true);
                    }
                });
                semaphore.acquireReq().send(messageProcessor,
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
        final JAContext jaContext = new JAContext();
        messageProcessor = new NonBlockingMessageProcessor(jaContext);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingMessageProcessor(jaContext), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, jaContext);
        final boolean result = acquireException(semaphore,
                new NonBlockingMessageProcessor(jaContext)).call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        assertTrue(result);
        jaContext.close();
    }
}
