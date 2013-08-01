package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.AtomicMailbox;
import org.agilewiki.jactor2.core.mailbox.Mailbox;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testa() throws Exception {
        final JAContext jaContext = new JAContext();
        final Mailbox mailbox = new AtomicMailbox(jaContext);
        final Actor1 actor1 = new Actor1(mailbox);
        final String result = actor1.hi.call();
        assertEquals("Hello world!", result);
        jaContext.close();
    }
}
