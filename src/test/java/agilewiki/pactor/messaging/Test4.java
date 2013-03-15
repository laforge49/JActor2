package agilewiki.pactor.messaging;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testa() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        Actor1 actor1 = new Actor1(mailbox);
        Actor4 actor4 = new Actor4(mailbox);
        actor4.hi4(actor1).send();
        mailboxFactory.shutdown();
    }

    public void testb() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        Actor1 actor1 = new Actor1(mailbox);
        Actor4 actor4 = new Actor4(mailbox);
        actor4.hi4(actor1).pend();
        mailboxFactory.shutdown();
    }
}
