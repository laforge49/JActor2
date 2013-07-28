package org.agilewiki.jactor2.general.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.api.Mailbox;
import org.agilewiki.jactor2.api.MailboxFactory;
import org.agilewiki.jactor2.api.ResponseProcessor;
import org.agilewiki.jactor2.api.ThreadBoundMailbox;
import org.agilewiki.jactor2.impl.DefaultMailboxFactory;

/**
 * Test code.
 */
public class ThreadBoundTest extends TestCase {
    ThreadBoundMailbox boundMailbox;
    MailboxFactory mailboxFactory;

    public void testa() throws Exception {
        mailboxFactory = new DefaultMailboxFactory();
        boundMailbox = mailboxFactory.createThreadBoundMailbox(new Runnable() {
            @Override
            public void run() {
                boundMailbox.run();
                try {
                    mailboxFactory.close();
                } catch (final Throwable x) {
                }
            }
        });
        final Mailbox mailbox = mailboxFactory.createAtomicMailbox();
        final Actor1 actor1 = new Actor1(mailbox);
        actor1.hi.send(boundMailbox, new ResponseProcessor<String>() {
            @Override
            public void processResponse(final String response) throws Exception {
                System.out.println(response);
                assertEquals("Hello world!", response);
            }
        });
    }
}
