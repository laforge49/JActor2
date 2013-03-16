package agilewiki.pactor.messaging;

import junit.framework.TestCase;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testa() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Mailbox mailbox = mailboxFactory.createMailbox();
        final Actor3 actor3 = new Actor3(mailbox);
        actor3.hi3().send();
        mailboxFactory.shutdown();
    }

    public void testb() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Mailbox mailbox = mailboxFactory.createMailbox();
        final Actor3 actor3 = new Actor3(mailbox);
        actor3.hi3().pend();
        mailboxFactory.shutdown();
    }
}
