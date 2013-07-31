package org.agilewiki.jactor2.general.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Mailbox;
import org.agilewiki.jactor2.core.MailboxFactory;
import org.agilewiki.jactor2.impl.DefaultMailboxFactory;

/**
 * Test code.
 */
public class Test5 extends TestCase {
    public void testCascading() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactory();
        final Mailbox mailboxE = mailboxFactory.createAtomicMailbox();
        final Mailbox mailboxA = mailboxFactory.createAtomicMailbox();
        final ActorE actorE = new ActorE(mailboxE);
        final ActorA actorA = new ActorA(mailboxA);
        try {
            actorE.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            // It's magic! We get the SecurityException, although our request
            // did not throw it, or return it as response. This shows that
            // child request exceptions are passed up to the parent request.
            mailboxFactory.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }
}
