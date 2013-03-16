package agilewiki.pactor.messaging;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testa() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        Actor1 actor1 = new Actor1(mailbox);
        Actor2 actor2 = new Actor2(mailbox);
        String result = actor2.hi2(actor1).pend();
        assertEquals("Hello world!", result);
        mailboxFactory.shutdown();
    }

    public void testb() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        Actor1 actor1 = new Actor1(mailbox);
        Actor2 actor2 = new Actor2(mailbox);
        actor2.hi2(actor1).send();
        mailboxFactory.shutdown();
    }

    public void testc() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Actor1 actor1 = new Actor1(mailboxFactory.createMailbox());
        Actor2 actor2 = new Actor2(mailboxFactory.createMailbox());
        String result = actor2.hi2(actor1).pend();
        assertEquals("Hello world!", result);
        mailboxFactory.shutdown();
    }

    public void testd() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Actor1 actor1 = new Actor1(mailboxFactory.createMailbox());
        Actor2 actor2 = new Actor2(mailboxFactory.createMailbox());
        actor2.hi2(actor1).send();
        mailboxFactory.shutdown();
    }
}
