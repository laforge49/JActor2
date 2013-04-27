package org.agilewiki.general.messaging;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMailbox(true);
        final Actor3 actor3 = new Actor3(mailbox);
        actor3.hi3.call();
        mailboxFactory.close();
    }
}
