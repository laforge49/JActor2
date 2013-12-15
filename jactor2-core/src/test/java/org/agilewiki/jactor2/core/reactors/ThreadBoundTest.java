package org.agilewiki.jactor2.core.reactors;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.Blade1;

/**
 * Test code.
 */
public class ThreadBoundTest extends TestCase {
    ThreadBoundReactor boundReactor;

    public void testa() throws Exception {
        final Plant plant = new Plant();
        try {
            boundReactor = new ThreadBoundReactor(plant, new Runnable() {
                @Override
                public void run() {
                    boundReactor.run();
                    try {
                        plant.close();
                    } catch (final Throwable x) {
                    }
                }
            });
            final Reactor reactor = new IsolationReactor(plant);
            final Blade1 blade1 = new Blade1(reactor);
            String response = blade1.hiSReq().call();
            System.out.println(response);
            assertEquals("Hello world!", response);
        } finally {
            plant.close();
        }
    }
}
