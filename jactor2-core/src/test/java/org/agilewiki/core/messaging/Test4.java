package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final JAContext jaContext = new JAContext();
        final Mailbox mailbox = jaContext.createNonBlockingMailbox();
        final Actor1 actor1 = new Actor1(mailbox);
        final Actor4 actor4 = new Actor4(mailbox);
        actor4.hi4(actor1).call();
        jaContext.close();
    }

    public void testd() throws Exception {
        final JAContext jaContext = new JAContext();
        final Actor1 actor1 = new Actor1(jaContext.createAtomicMailbox());
        final Actor4 actor4 = new Actor4(jaContext.createAtomicMailbox());
        actor4.hi4(actor1).call();
        jaContext.close();
    }
}
