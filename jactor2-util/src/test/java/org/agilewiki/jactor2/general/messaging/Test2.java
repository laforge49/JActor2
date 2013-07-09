package org.agilewiki.jactor2.general.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.MailboxFactory;
import org.agilewiki.jactor2.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testa() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMayBlockMailbox();
        final Actor1 actor1 = new Actor1(mailbox);
        final Actor2 actor2 = new Actor2(mailbox);
        final String result = actor2.hi2(actor1).call();
        assertEquals("Hello world!", result);
        mailboxFactory.close();
    }

    public void testc() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Actor1 actor1 = new Actor1(mailboxFactory.createMayBlockMailbox());
        final Actor2 actor2 = new Actor2(mailboxFactory.createMayBlockMailbox());
        final String result = actor2.hi2(actor1).call();
        assertEquals("Hello world!", result);
        mailboxFactory.close();
    }
}
