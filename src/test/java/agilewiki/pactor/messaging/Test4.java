package agilewiki.pactor.messaging;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testa() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Mailbox mailbox = mailboxFactory.createMailbox();
        final Actor1 actor1 = new Actor1(mailbox);
        final Actor4 actor4 = new Actor4(mailbox);
        actor4.hi4(actor1).send();
        mailboxFactory.shutdown();
    }

    public void testb() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Mailbox mailbox = mailboxFactory.createMailbox();
        final Actor1 actor1 = new Actor1(mailbox);
        final Actor4 actor4 = new Actor4(mailbox);
        actor4.hi4(actor1).pend();
        mailboxFactory.shutdown();
    }

    public void testc() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Actor1 actor1 = new Actor1(mailboxFactory.createMailbox());
        final Actor4 actor4 = new Actor4(mailboxFactory.createMailbox());
        actor4.hi4(actor1).send();
        mailboxFactory.shutdown();
    }

    public void testd() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Actor1 actor1 = new Actor1(mailboxFactory.createMailbox());
        final Actor4 actor4 = new Actor4(mailboxFactory.createMailbox());
        actor4.hi4(actor1).pend();
        mailboxFactory.shutdown();
    }
}
