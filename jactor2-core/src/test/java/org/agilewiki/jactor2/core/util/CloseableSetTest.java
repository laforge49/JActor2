package org.agilewiki.jactor2.core.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class CloseableSetTest extends TestCase {

    public void testReactor() throws Exception {
        System.out.println("R");
        final Plant plant = new Plant();
        try {
            final Reactor reactor = new NonBlockingReactor();

            final MyCloseable mac1 = new MyCloseable();
            final MyCloseable mac2 = new MyCloseable();
            final MyCloseable mac3 = new MyCloseable();
            final MyCloseable mac4 = new MyCloseable();
            final MyFailedCloseable mfac = new MyFailedCloseable();
            reactor.addCloseable(mac1);
            reactor.addCloseable(mac2);
            reactor.addCloseable(mac3);
            reactor.addCloseable(mac4);
            reactor.addCloseable(mfac);
            reactor.removeCloseable(mac4);

            reactor.close();

            assertEquals(mac1.closed, 1);
            assertEquals(mac2.closed, 1);
            assertEquals(mac3.closed, 1);
            assertEquals(mac4.closed, 0);
            assertEquals(mfac.closed, 1);
        } finally {
            try {
                plant.close();
            } catch (final Throwable t) {
                // NOP
            }
        }
    }
}

class MyCloseable extends Closeable {
    public volatile int closed;

    MyCloseable() throws Exception {
        initialize(new NonBlockingReactor());
    }

    @Override
    public void close() throws Exception {
        closed++;
        super.close();
    }
}

class MyFailedCloseable extends MyCloseable {

    MyFailedCloseable() throws Exception {
        super();
    }

    @Override
    public void close() throws Exception {
        super.close();
        throw new IllegalStateException("FAIL!");
    }
}
