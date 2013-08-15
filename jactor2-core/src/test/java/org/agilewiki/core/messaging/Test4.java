package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final JAContext jaContext = new JAContext();
        final MessageProcessor messageProcessor = new NonBlockingMessageProcessor(jaContext);
        final Actor1 actor1 = new Actor1(messageProcessor);
        final Actor4 actor4 = new Actor4(messageProcessor);
        actor4.hi4(actor1).call();
        jaContext.close();
    }

    public void testd() throws Exception {
        final JAContext jaContext = new JAContext();
        final Actor1 actor1 = new Actor1(new AtomicMessageProcessor(jaContext));
        final Actor4 actor4 = new Actor4(new AtomicMessageProcessor(jaContext));
        actor4.hi4(actor1).call();
        jaContext.close();
    }
}
