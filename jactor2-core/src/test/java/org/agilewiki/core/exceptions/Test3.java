package org.agilewiki.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.processing.AtomicMailbox;
import org.agilewiki.jactor2.core.processing.Mailbox;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testI() throws Exception {
        final JAContext jaContext = new JAContext();
        final Mailbox mailbox = new AtomicMailbox(jaContext);
        final ActorC actorC = new ActorC(mailbox);
        final String result = actorC.throwRequest.call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        jaContext.close();
    }
}
