package org.agilewiki.jactor.general.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMailbox(true);
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
        final ActorA actorA = new ActorA(mailboxFactory.createMailbox(true));
        final ActorB actorB = new ActorB(mailboxFactory.createMailbox(true));
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
