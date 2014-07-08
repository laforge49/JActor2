package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

/**
 * Test code.
 */
public class ThreadBoundTest extends CallTestBase {
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
            final String response = blade1.hiSOp().call();
            System.out.println(response);
            assertEquals("Hello world!", response);
        } finally {
            Plant.close();
        }
    }
}
