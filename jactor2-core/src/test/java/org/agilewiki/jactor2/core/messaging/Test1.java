package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testa() throws Exception {
        final JAContext jaContext = new JAContext();
        final MessageProcessor messageProcessor = new AtomicMessageProcessor(jaContext);
        final Actor1 actor1 = new Actor1(messageProcessor);
        final String result = actor1.hi.call();
        assertEquals("Hello world!", result);
        jaContext.close();
    }
}
