package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMailbox;
import org.agilewiki.jactor2.core.processing.Mailbox;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final JAContext jaContext = new JAContext();
        final Mailbox mailbox = new AtomicMailbox(jaContext);
        final Actor3 actor3 = new Actor3(mailbox);
        actor3.hi3.call();
        jaContext.close();
    }
}
