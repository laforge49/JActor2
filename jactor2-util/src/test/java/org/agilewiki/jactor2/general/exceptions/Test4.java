package org.agilewiki.jactor2.general.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.DefaultMailboxFactory;
import org.agilewiki.jactor2.core.Mailbox;
import org.agilewiki.jactor2.core.MailboxFactory;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testI() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactory();
        final Mailbox mailbox = mailboxFactory.createAtomicMailbox();
        final ActorD actorD = new ActorD(mailbox);
        final String result = actorD.throwRequest.call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        mailboxFactory.close();
    }
}
