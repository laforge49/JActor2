package org.agilewiki.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMailbox;
import org.agilewiki.jactor2.core.processing.Mailbox;
import org.agilewiki.jactor2.core.processing.NonBlockingMailbox;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final JAContext jaContext = new JAContext();
        final Mailbox mailbox = new NonBlockingMailbox(jaContext);
        final ActorA actorA = new ActorA(mailbox);
        final ActorB actorB = new ActorB(mailbox);
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
        final ActorA actorA = new ActorA(new AtomicMailbox(jaContext));
        final ActorB actorB = new ActorB(new AtomicMailbox(jaContext));
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
