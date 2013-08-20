package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final JAContext jaContext = new JAContext();
        final MessageProcessor messageProcessor = new AtomicMessageProcessor(jaContext);
        final Actor3 actor3 = new Actor3(messageProcessor);
        actor3.hi3.call();
        jaContext.close();
    }
}
