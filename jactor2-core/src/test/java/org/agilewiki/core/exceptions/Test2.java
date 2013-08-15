package org.agilewiki.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final JAContext jaContext = new JAContext();
        final MessageProcessor messageProcessor = new NonBlockingMessageProcessor(jaContext);
        final ActorA actorA = new ActorA(messageProcessor);
        final ActorB actorB = new ActorB(messageProcessor);
        try {
            actorB.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            jaContext.close();
            return;
        }
        jaContext.close();
        throw new Exception("Security exception was not caught");
    }

    public void testIII() throws Exception {
        System.out.println("testIII");
        final JAContext jaContext = new JAContext();
        final ActorA actorA = new ActorA(new AtomicMessageProcessor(jaContext));
        final ActorB actorB = new ActorB(new AtomicMessageProcessor(jaContext));
        try {
            actorB.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            jaContext.close();
            return;
        }
        jaContext.close();
        throw new Exception("Security exception was not caught");
    }
}
