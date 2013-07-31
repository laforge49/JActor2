package org.agilewiki.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final JAContext jaContext = new JAContext();
        final Mailbox mailbox = jaContext.createNonBlockingMailbox();
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
        final ActorA actorA = new ActorA(jaContext.createAtomicMailbox());
        final ActorB actorB = new ActorB(jaContext.createAtomicMailbox());
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
