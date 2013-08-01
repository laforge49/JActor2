package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.AtomicMailbox;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.mailbox.NonBlockingMailbox;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final JAContext jaContext = new JAContext();
        final Mailbox mailbox = new NonBlockingMailbox(jaContext);
        final Actor1 actor1 = new Actor1(mailbox);
        final Actor4 actor4 = new Actor4(mailbox);
        actor4.hi4(actor1).call();
        jaContext.close();
    }

    public void testd() throws Exception {
        final JAContext jaContext = new JAContext();
        final Actor1 actor1 = new Actor1(new AtomicMailbox(jaContext));
        final Actor4 actor4 = new Actor4(new AtomicMailbox(jaContext));
        actor4.hi4(actor1).call();
        jaContext.close();
    }
}
