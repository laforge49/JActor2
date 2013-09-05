package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Delay;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class ParallelTest extends TestCase {
    private static final int LOADS = 10;
    private static final long DELAY = 200;

    private MessageProcessor messageProcessor;
    private ModuleContext moduleContext;
    private Request<Void> start;

    public void test() throws Exception {
        moduleContext = new ModuleContext();
        messageProcessor = new NonBlockingMessageProcessor(moduleContext);

        start = new Request<Void>(messageProcessor) {
            Request<Void> dis = this;
            @Override
            public void processRequest()
                    throws Exception {
                final ResponseCounter<Void> responseCounter = new ResponseCounter<Void>(
                        LOADS, null, dis);
                int i = 0;
                while (i < LOADS) {
                    final Delay dly = new Delay(moduleContext);
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
        moduleContext.close();
    }
}
