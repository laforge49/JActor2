package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final JAContext jaContext = new JAContext();
        final MessageProcessor messageProcessor = new AtomicMessageProcessor(jaContext);
        final ActorA actorA = new ActorA(messageProcessor);
        try {
            actorA.throwRequest.call();
        } catch (final SecurityException se) {
            jaContext.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }

}
