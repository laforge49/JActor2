package org.agilewiki.jactor2.core.util;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class AutoCloseableSetTest extends TestCase {
    static volatile int closed;

    private static class MyAutoCloseable implements AutoCloseable {
        public volatile int closed;

        /* (non-Javadoc)
         * @see java.lang.AutoCloseable#close()
         */
        @Override
        public void close() throws Exception {
            closed++;
            AutoCloseableSetTest.closed++;
        }
    }

    private static class MyFailedAutoCloseable extends MyAutoCloseable {
        /* (non-Javadoc)
         * @see java.lang.AutoCloseable#close()
         */
        @Override
        public void close() throws Exception {
            super.close();
            throw new IllegalStateException("FAIL!");
        }
    }

    public void testSet() throws Exception {
        /*
        final AutoCloseableSet set = new AutoCloseableSet();
        final MyAutoCloseable mac1 = new MyAutoCloseable();
        final MyAutoCloseable mac2 = new MyAutoCloseable();
        final MyAutoCloseable mac3 = new MyAutoCloseable();
        final MyAutoCloseable mac4 = new MyAutoCloseable();
        final MyFailedAutoCloseable mfac = new MyFailedAutoCloseable();
        set.add(mac1);
        set.add(mac2);
        set.add(mac3);
        set.add(mac4);
        set.add(mfac);
        set.remove(mac4);
        int mac1Count = 0;
        int mac2Count = 0;
        int mac3Count = 0;
        int mac4Count = 0;
        int mfacCount = 0;
        int otherCount = 0;
        for (final AutoCloseable ac : set) {
            if (ac == mac1) {
                mac1Count++;
            } else if (ac == mac2) {
                mac2Count++;
            } else if (ac == mac3) {
                mac3Count++;
            } else if (ac == mac4) {
                mac4Count++;
            } else if (ac == mfac) {
                mfacCount++;
            } else {
                otherCount++;
            }
        }

        assertEquals(mac1Count, 1);
        assertEquals(mac2Count, 1);
        assertEquals(mac3Count, 1);
        assertEquals(mac4Count, 0);
        assertEquals(mfacCount, 1);
        assertEquals(otherCount, 0);

        set.close();
        set.close();
        assertEquals(mac1.closed, 1);
        assertEquals(mac2.closed, 1);
        assertEquals(mac3.closed, 1);
        assertEquals(mac4.closed, 0);
        assertEquals(mfac.closed, 1);

        boolean foundAny = false;
        for (final AutoCloseable ac : set) {
            foundAny = true;
        }
        assertFalse(foundAny);
        */
    }

    public void testFacility() throws Exception {
        /*
        // a Plant is also a Facility, so I only need to test the Plant ...
        final Plant plant = new Plant();
        try {
            final MyAutoCloseable mac1 = new MyAutoCloseable();
            final MyAutoCloseable mac2 = new MyAutoCloseable();
            final MyAutoCloseable mac3 = new MyAutoCloseable();
            final MyAutoCloseable mac4 = new MyAutoCloseable();
            final MyFailedAutoCloseable mfac = new MyFailedAutoCloseable();
            plant.addAutoClosableSReq(mac1).signal();
            plant.addAutoClosableSReq(mac2).signal();
            plant.addAutoClosableSReq(mac3).signal();
            plant.addAutoClosableSReq(mac4).signal();
            plant.addAutoClosableSReq(mfac).signal();
            plant.removeAutoClosableSReq(mac4).call();
            final int before = closed;
            plant.close();
            // You cannot wait on close() ...
            int count = 0;
            while ((closed < before - 4) && (count < 10)) {
                Thread.sleep(100);
                count++;
            }
            assertEquals(mac1.closed, 1);
            assertEquals(mac2.closed, 1);
            assertEquals(mac3.closed, 1);
            assertEquals(mac4.closed, 0);
            assertEquals(mfac.closed, 1);
        } finally {
            // Close it again, just in case ...
            try {
                plant.close();
            } catch (final Throwable t) {
                // NOP
            }
        }
        */
    }

    public void testReactor() throws Exception {
        /*
        final Plant plant = new Plant();
        try {
            final Reactor reactor = new NonBlockingReactor(plant);

            final MyAutoCloseable mac1 = new MyAutoCloseable();
            final MyAutoCloseable mac2 = new MyAutoCloseable();
            final MyAutoCloseable mac3 = new MyAutoCloseable();
            final MyAutoCloseable mac4 = new MyAutoCloseable();
            final MyFailedAutoCloseable mfac = new MyFailedAutoCloseable();
            reactor.addAutoClosableSReq(mac1).signal();
            reactor.addAutoClosableSReq(mac2).signal();
            reactor.addAutoClosableSReq(mac3).signal();
            reactor.addAutoClosableSReq(mac4).signal();
            reactor.addAutoClosableSReq(mfac).signal();
            reactor.removeAutoClosableSReq(mac4).call();
            final int before = closed;
            reactor.close();
            // You cannot wait on close() ...
            int count = 0;
            while ((closed < before - 4) && (count < 10)
                    && !reactor.isClosing()) {
                Thread.sleep(100);
                count++;
            }
            assertEquals(mac1.closed, 1);
            assertEquals(mac2.closed, 1);
            assertEquals(mac3.closed, 1);
            assertEquals(mac4.closed, 0);
            assertEquals(mfac.closed, 1);
        } finally {
            // Close it again, just in case ...
            try {
                plant.close();
            } catch (final Throwable t) {
                // NOP
            }
        }
        */
    }
}
