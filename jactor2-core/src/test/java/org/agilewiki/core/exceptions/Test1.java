package org.agilewiki.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.DefaultMailboxFactory;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.context.MailboxFactory;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactory();
        final Mailbox mailbox = mailboxFactory.createAtomicMailbox();
        final ActorA actorA = new ActorA(mailbox);
        try {
            actorA.throwRequest.call();
        } catch (final SecurityException se) {
            mailboxFactory.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }

}
