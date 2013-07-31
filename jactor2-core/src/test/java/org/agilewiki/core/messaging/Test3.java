package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.DefaultMailboxFactory;
import org.agilewiki.jactor2.core.Mailbox;
import org.agilewiki.jactor2.core.MailboxFactory;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactory();
        final Mailbox mailbox = mailboxFactory.createAtomicMailbox();
        final Actor3 actor3 = new Actor3(mailbox);
        actor3.hi3.call();
        mailboxFactory.close();
    }
}
