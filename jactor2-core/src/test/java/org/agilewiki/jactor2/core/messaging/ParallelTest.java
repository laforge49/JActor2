package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Delay;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

/**
 * Test code.
 */
public class ParallelTest extends TestCase {
    private static final int LOADS = 10;
    private static final long DELAY = 200;

    private MessageProcessor messageProcessor;
    private JAContext jaContext;
    private Request<Void> start;

    public void test() throws Exception {
        jaContext = new JAContext();
        messageProcessor = new NonBlockingMessageProcessor(jaContext);

        start = new Request<Void>(messageProcessor) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                final ResponseCounter<Void> responseCounter = new ResponseCounter<Void>(
                        LOADS, null, responseProcessor);
                int i = 0;
                while (i < LOADS) {
                    final Delay dly = new Delay(new AtomicMessageProcessor(jaContext));
                    dly.sleepReq(ParallelTest.DELAY).send(messageProcessor,
                            responseCounter);
                    i += 1;
                }
            }
        };

        final long t0 = System.currentTimeMillis();
        start.call();
        final long t1 = System.currentTimeMillis();
        assertTrue((t1 - t0) < DELAY + DELAY / 2);
        jaContext.close();
    }
}
