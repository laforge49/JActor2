package org.agilewiki.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.AtomicMailbox;
import org.agilewiki.jactor2.core.mailbox.Mailbox;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testI() throws Exception {
        final JAContext jaContext = new JAContext();
        final Mailbox mailbox = new AtomicMailbox(jaContext);
        final ActorD actorD = new ActorD(mailbox);
        final String result = actorD.throwRequest.call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        jaContext.close();
    }
}
