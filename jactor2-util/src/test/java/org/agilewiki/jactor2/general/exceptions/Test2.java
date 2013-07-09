package org.agilewiki.jactor2.general.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.MailboxFactory;
import org.agilewiki.jactor2.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMayBlockMailbox();
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
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final ActorA actorA = new ActorA(mailboxFactory.createMayBlockMailbox());
        final ActorB actorB = new ActorB(mailboxFactory.createMayBlockMailbox());
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
