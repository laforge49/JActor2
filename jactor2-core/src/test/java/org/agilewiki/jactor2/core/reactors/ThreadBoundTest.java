package org.agilewiki.jactor2.core.reactors;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.BasicPlant;

/**
 * Test code.
 */
public class ThreadBoundTest extends TestCase {
    ThreadBoundReactor reactor;

    public void testa() throws Exception {
        final BasicPlant plant = new BasicPlant();
        try {
            reactor = new ThreadBoundReactor(new Runnable() {
                @Override
                public void run() {
                    reactor.run();
                    try {
                        plant.close();
                    } catch (final Throwable x) {
                    }
                }
            });
            final Blade11 blade1 = new Blade11(reactor);
            String response = blade1.hiSReq().call();
            System.out.println(response);
            assertEquals("Hello world!", response);
        } finally {
            plant.close();
        }
    }
}
