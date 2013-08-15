package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.processing.AtomicMailbox;
import org.agilewiki.jactor2.core.processing.Mailbox;
import org.agilewiki.jactor2.core.processing.ThreadBoundMailbox;

/**
 * Test code.
 */
public class ThreadBoundTest extends TestCase {
    ThreadBoundMailbox boundMailbox;
    JAContext jaContext;

    public void testa() throws Exception {
        jaContext = new JAContext();
        boundMailbox = new ThreadBoundMailbox(jaContext, new Runnable() {
            @Override
            public void run() {
                boundMailbox.run();
                try {
                    jaContext.close();
                } catch (final Throwable x) {
                }
            }
        });
        final Mailbox mailbox = new AtomicMailbox(jaContext);
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
