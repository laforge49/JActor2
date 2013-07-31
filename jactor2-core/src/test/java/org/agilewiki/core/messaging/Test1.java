package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.DefaultMailboxFactory;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.context.MailboxFactory;

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
