package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testa() throws Exception {
        System.out.println("testa");
        final JAContext jaContext = new JAContext();
        final MessageProcessor messageProcessor = new NonBlockingMessageProcessor(jaContext);
        final Actor1 actor1 = new Actor1(messageProcessor);
        final Actor2 actor2 = new Actor2(messageProcessor);
        final String result = actor2.hi2(actor1).call();
        assertEquals("Hello world!", result);
        jaContext.close();
    }

    public void testc() throws Exception {
        System.out.println("testb");
        final JAContext jaContext = new JAContext();
        final Actor1 actor1 = new Actor1(new AtomicMessageProcessor(jaContext));
        final Actor2 actor2 = new Actor2(new AtomicMessageProcessor(jaContext));
        final String result = actor2.hi2(actor1).call();
        assertEquals("Hello world!", result);
        jaContext.close();
    }
}
