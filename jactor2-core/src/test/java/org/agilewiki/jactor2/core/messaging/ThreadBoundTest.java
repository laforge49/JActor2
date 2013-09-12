package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.processing.ThreadBoundReactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class ThreadBoundTest extends TestCase {
    ThreadBoundReactor boundMailbox;
    Facility facility;

    public void testa() throws Exception {
        facility = new Facility();
        boundMailbox = new ThreadBoundReactor(facility, new Runnable() {
            @Override
            public void run() {
                boundMailbox.run();
                try {
                    facility.close();
                } catch (final Throwable x) {
                }
            }
        });
        final Reactor reactor = new IsolationReactor(facility);
        final Actor1 actor1 = new Actor1(reactor);
        actor1.hiSReq().send(boundMailbox, new AsyncResponseProcessor<String>() {
            @Override
            public void processAsyncResponse(final String response) throws Exception {
                System.out.println(response);
                assertEquals("Hello world!", response);
            }
        });
    }
}
