package org.agilewiki.jactor2.general.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.MailboxFactory;
import org.agilewiki.jactor2.impl.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMayBlockMailbox();
        final ActorA actorA = new ActorA(mailbox);
        try {
            actorA.throwBoundRequest.call();
        } catch (final SecurityException se) {
            mailboxFactory.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }

}
