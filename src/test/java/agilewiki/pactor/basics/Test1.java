package agilewiki.pactor.basics;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void test() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        Actor1 actor1 = new Actor1(mailbox);
        String result = actor1.hi1().pend();
        assertEquals("Hello world!", result);
        mailboxFactory.shutdown();
    }
}
