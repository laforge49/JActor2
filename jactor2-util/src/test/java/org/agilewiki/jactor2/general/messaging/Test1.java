package org.agilewiki.jactor2.general.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.MailboxFactory;
import org.agilewiki.jactor2.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testa() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMayBlockMailbox();
        final Actor1 actor1 = new Actor1(mailbox);
        final String result = actor1.hi1.call();
        assertEquals("Hello world!", result);
        mailboxFactory.close();
    }
}
