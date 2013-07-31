package org.agilewiki.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.DefaultMailboxFactory;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.context.MailboxFactory;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final MailboxFactory mailboxFactory = new DefaultMailboxFactory();
        final Mailbox mailbox = mailboxFactory.createNonBlockingMailbox();
        final ActorA actorA = new ActorA(mailbox);
        final ActorB actorB = new ActorB(mailbox);
        try {
            actorB.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            mailboxFactory.close();
            return;
        }
        mailboxFactory.close();
        throw new Exception("Security exception was not caught");
    }

    public void testIII() throws Exception {
        System.out.println("testIII");
        final MailboxFactory mailboxFactory = new DefaultMailboxFactory();
        final ActorA actorA = new ActorA(mailboxFactory.createAtomicMailbox());
        final ActorB actorB = new ActorB(mailboxFactory.createAtomicMailbox());
        try {
            actorB.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            mailboxFactory.close();
            return;
        }
        mailboxFactory.close();
        throw new Exception("Security exception was not caught");
    }
}
