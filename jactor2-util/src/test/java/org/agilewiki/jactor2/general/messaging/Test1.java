package org.agilewiki.jactor2.general.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.DefaultMailboxFactory;
import org.agilewiki.jactor2.core.Mailbox;
import org.agilewiki.jactor2.core.MailboxFactory;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testa() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactory();
        final Mailbox mailbox = mailboxFactory.createAtomicMailbox();
        final Actor1 actor1 = new Actor1(mailbox);
        final String result = actor1.hi.call();
        assertEquals("Hello world!", result);
        mailboxFactory.close();
    }
}
