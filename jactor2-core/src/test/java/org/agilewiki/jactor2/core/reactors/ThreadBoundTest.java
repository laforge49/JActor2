package org.agilewiki.jactor2.core.reactors;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.Blade1;

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
        final Blade1 blade1 = new Blade1(reactor);
        blade1.hiSReq().send(boundMailbox, new AsyncResponseProcessor<String>() {
            @Override
            public void processAsyncResponse(final String response) throws Exception {
                System.out.println(response);
                assertEquals("Hello world!", response);
            }
        });
    }
}
