package org.agilewiki.jactor.general.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testI() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMailbox(true);
        final ActorC actorC = new ActorC(mailbox);
        final String result = actorC.throwRequest.call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        mailboxFactory.close();
    }
}
