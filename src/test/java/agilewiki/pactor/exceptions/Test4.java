package agilewiki.pactor.exceptions;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testI() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Mailbox mailbox = mailboxFactory.createMailbox();
        final ActorD actorD = new ActorD(mailbox);
        final String result = actorD.throwRequest.pend();
        assertEquals("java.lang.SecurityException: thrown on request", result);
    }

    public void testII() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Mailbox mailbox = mailboxFactory.createMailbox();
        final ActorD actorD = new ActorD(mailbox);
        actorD.throwRequest.send();
    }
}
