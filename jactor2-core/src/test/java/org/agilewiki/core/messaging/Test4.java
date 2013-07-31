package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final JAContext mailboxFactory = new JAContext();
        final Mailbox mailbox = mailboxFactory.createNonBlockingMailbox();
        final Actor1 actor1 = new Actor1(mailbox);
        final Actor4 actor4 = new Actor4(mailbox);
        actor4.hi4(actor1).call();
        mailboxFactory.close();
    }

    public void testd() throws Exception {
        final JAContext mailboxFactory = new JAContext();
        final Actor1 actor1 = new Actor1(mailboxFactory.createAtomicMailbox());
        final Actor4 actor4 = new Actor4(mailboxFactory.createAtomicMailbox());
        actor4.hi4(actor1).call();
        mailboxFactory.close();
    }
}
