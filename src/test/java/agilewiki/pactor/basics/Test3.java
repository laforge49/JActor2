package agilewiki.pactor.basics;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void test() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        Actor3 actor3 = new Actor3(mailbox);
        actor3.hi3().send();
        mailboxFactory.shutdown();
    }
}
