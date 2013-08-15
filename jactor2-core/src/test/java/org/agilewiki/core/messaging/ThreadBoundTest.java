package org.agilewiki.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.ThreadBoundMessageProcessor;

/**
 * Test code.
 */
public class ThreadBoundTest extends TestCase {
    ThreadBoundMessageProcessor boundMailbox;
    JAContext jaContext;

    public void testa() throws Exception {
        jaContext = new JAContext();
        boundMailbox = new ThreadBoundMessageProcessor(jaContext, new Runnable() {
            @Override
            public void run() {
                boundMailbox.run();
                try {
                    jaContext.close();
                } catch (final Throwable x) {
                }
            }
        });
        final MessageProcessor messageProcessor = new AtomicMessageProcessor(jaContext);
        final Actor1 actor1 = new Actor1(messageProcessor);
        actor1.hi.send(boundMailbox, new ResponseProcessor<String>() {
            @Override
            public void processResponse(final String response) throws Exception {
                System.out.println(response);
                assertEquals("Hello world!", response);
            }
        });
    }
}
