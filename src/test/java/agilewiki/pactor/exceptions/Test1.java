package agilewiki.pactor.exceptions;

import junit.framework.TestCase;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Mailbox mailbox = mailboxFactory.createMailbox();
        final ActorA actorA = new ActorA(mailbox);
        try {
            actorA.throwRequest().pend();
        } catch (final SecurityException se) {
            mailboxFactory.shutdown();
            return;
        }
        throw new Exception("Security exception was not caught");
    }

    /**
     * A SecurityException should be logged.
     */
    public void testII() throws Exception {
        final MailboxFactory mailboxFactory = new MailboxFactory();
        final Mailbox mailbox = mailboxFactory.createMailbox();
        final ActorA actorA = new ActorA(mailbox);
        actorA.throwRequest().send();
        mailboxFactory.shutdown();
    }
}
