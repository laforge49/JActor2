package org.agilewiki.pactor.general.messaging;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMailbox(true);
        final Actor1 actor1 = new Actor1(mailbox);
        final Actor4 actor4 = new Actor4(mailbox);
        actor4.hi4(actor1).call();
        mailboxFactory.close();
    }

    public void testd() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Actor1 actor1 = new Actor1(mailboxFactory.createMailbox(true));
        final Actor4 actor4 = new Actor4(mailboxFactory.createMailbox(true));
        actor4.hi4(actor1).call();
        mailboxFactory.close();
    }
}
