package org.agilewiki.jactor2.core.mt.reactors;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Plant;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

/**
 * Test code.
 */
public class ThreadBoundTest extends TestCase {
    ThreadBoundReactor reactor;

    public void testa() throws Exception {
        new Plant();
        try {
            reactor = new ThreadBoundReactor(new Runnable() {
                @Override
                public void run() {
                    reactor.run();
                    try {
                        Plant.close();
                    } catch (final Throwable x) {
                    }
                }
            });
            final Blade11 blade1 = new Blade11(reactor);
            String response = blade1.hiSReq().call();
            System.out.println(response);
            assertEquals("Hello world!", response);
        } finally {
            Plant.close();
        }
    }
}
