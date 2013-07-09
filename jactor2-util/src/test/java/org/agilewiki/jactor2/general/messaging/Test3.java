package org.agilewiki.jactor2.general.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.MailboxFactory;
import org.agilewiki.jactor2.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMayBlockMailbox();
        final Actor3 actor3 = new Actor3(mailbox);
        actor3.hi3.call();
        mailboxFactory.close();
    }
}
