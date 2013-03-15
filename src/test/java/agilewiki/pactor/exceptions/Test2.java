package agilewiki.pactor.exceptions;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        ActorA actorA = new ActorA(mailbox);
        ActorB actorB = new ActorB(mailbox);
        try {
            actorB.throwRequest(actorA).pend();
        } catch (SecurityException se) {
            mailboxFactory.shutdown();
            return;
        }
        throw new Exception("Security exception was not caught");
    }

    /**
     * A SecurityException should be logged.
     */
    public void testII() throws Throwable {
        MailboxFactory mailboxFactory = new MailboxFactory();
        Mailbox mailbox = mailboxFactory.createMailbox();
        ActorA actorA = new ActorA(mailbox);
        ActorB actorB = new ActorB(mailbox);
        actorB.throwRequest(actorA).send();
    }
}
