package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testI() throws Exception {
        final JAContext jaContext = new JAContext();
        final MessageProcessor messageProcessor = new AtomicMessageProcessor(jaContext);
        final ActorC actorC = new ActorC(messageProcessor);
        final String result = actorC.throwRequest.call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        jaContext.close();
    }
}
