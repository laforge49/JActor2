package org.agilewiki.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testI() throws Exception {
        final JAContext mailboxFactory = new JAContext();
        final Mailbox mailbox = mailboxFactory.createAtomicMailbox();
        final ActorC actorC = new ActorC(mailbox);
        final String result = actorC.throwRequest.call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        mailboxFactory.close();
    }
}
