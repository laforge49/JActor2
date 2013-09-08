package org.agilewiki.jactor2.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Actor;
import org.agilewiki.jactor2.core.Delay;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messaging.Event;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

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
        final ModuleContext moduleContext = new ModuleContext();
        messageProcessor = new NonBlockingMessageProcessor(moduleContext);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingMessageProcessor(moduleContext), 1);
        semaphore.acquireReq().call();
        moduleContext.close();
    }

    public void testII() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        messageProcessor = new NonBlockingMessageProcessor(moduleContext);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingMessageProcessor(moduleContext), 0);
        semaphore.release();
        semaphore.acquireReq().call();
        moduleContext.close();
    }

    private void delayedRelease(final JASemaphore semaphore,
                                final long delay,
                                final ModuleContext moduleContext) throws Exception {
        new Event<SemaphoreTest>() {
            @Override
            public void processEvent(final SemaphoreTest actor)
                    throws Exception {
                new Delay(moduleContext).sleepReq(delay).send(getMessageProcessor(),
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
        final ModuleContext moduleContext = new ModuleContext();
        messageProcessor = new NonBlockingMessageProcessor(moduleContext);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingMessageProcessor(moduleContext), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, moduleContext);
        semaphore.acquireReq().call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        moduleContext.close();
    }

    private AsyncRequest<Boolean> acquireException(final JASemaphore semaphore,
                                              final MessageProcessor messageProcessor) {
        return new AsyncRequest<Boolean>(messageProcessor) {
            @Override
            public void processRequest()
                    throws Exception {
                messageProcessor.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        System.out.println(throwable);
                        processAsyncResponse(true);
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
        final ModuleContext moduleContext = new ModuleContext();
        messageProcessor = new NonBlockingMessageProcessor(moduleContext);
        final JASemaphore semaphore = new JASemaphore(
                new NonBlockingMessageProcessor(moduleContext), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, moduleContext);
        final boolean result = acquireException(semaphore,
                new NonBlockingMessageProcessor(moduleContext)).call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        assertTrue(result);
        moduleContext.close();
    }
}
