package org.agilewiki.jactor.general.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMayBlockMailbox();
        final Actor1 actor1 = new Actor1(mailbox);
        final Actor4 actor4 = new Actor4(mailbox);
        actor4.hi4(actor1).call();
        mailboxFactory.close();
    }

    public void testd() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Actor1 actor1 = new Actor1(mailboxFactory.createMayBlockMailbox());
        final Actor4 actor4 = new Actor4(mailboxFactory.createMayBlockMailbox());
        actor4.hi4(actor1).call();
        mailboxFactory.close();
    }
}
