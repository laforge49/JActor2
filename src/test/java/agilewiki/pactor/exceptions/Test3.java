package agilewiki.pactor.exceptions;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testI() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        ActorC actorC = new ActorC(mailbox);
        String result = actorC.throwRequest().pend();
        assertEquals("java.lang.SecurityException: thrown on request", result);
    }

    public void testII() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        ActorC actorC = new ActorC(mailbox);
        actorC.throwRequest().send();
    }
}
