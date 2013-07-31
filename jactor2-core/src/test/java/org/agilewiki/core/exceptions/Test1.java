package org.agilewiki.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final JAContext mailboxFactory = new JAContext();
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
