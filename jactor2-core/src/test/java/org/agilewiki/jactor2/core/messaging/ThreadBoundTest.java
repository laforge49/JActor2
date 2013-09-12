package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.ThreadBoundMessageProcessor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class ThreadBoundTest extends TestCase {
    ThreadBoundMessageProcessor boundMailbox;
    Facility facility;

    public void testa() throws Exception {
        facility = new Facility();
        boundMailbox = new ThreadBoundMessageProcessor(facility, new Runnable() {
            @Override
            public void run() {
                boundMailbox.run();
                try {
                    facility.close();
                } catch (final Throwable x) {
                }
            }
        });
        final MessageProcessor messageProcessor = new IsolationMessageProcessor(facility);
        final Actor1 actor1 = new Actor1(messageProcessor);
        actor1.hiSReq().send(boundMailbox, new AsyncResponseProcessor<String>() {
            @Override
            public void processAsyncResponse(final String response) throws Exception {
                System.out.println(response);
                assertEquals("Hello world!", response);
            }
        });
    }
}
