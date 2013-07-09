package org.agilewiki.jactor2.general.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.MailboxFactory;
import org.agilewiki.jactor2.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testI() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMayBlockMailbox();
        final ActorD actorD = new ActorD(mailbox);
        final String result = actorD.throwRequest.call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        mailboxFactory.close();
    }
}
