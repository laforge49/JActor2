package org.agilewiki.jactor2.core.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class CloseableSetTest extends TestCase {
    private static class MyAutoCloseable extends BladeBase implements Closeable {
        public volatile int closed;

        MyAutoCloseable(Plant plant) throws Exception {
            initialize(new NonBlockingReactor(plant));
        }

        /* (non-Javadoc)
         * @see java.lang.AutoCloseable#close()
         */
        @Override
        public void close() throws Exception {
            closed++;
        }

        @Override
        public SyncRequest<Boolean> addCloserSReq(Closer _closer) {
            return new SyncBladeRequest<Boolean>() {
                @Override
                protected Boolean processSyncRequest() throws Exception {
                    return true;
                }
            };
        }

        @Override
        public SyncRequest<Boolean> removeCloserSReq(Closer _closer) {
            return new SyncBladeRequest<Boolean>() {
                @Override
                protected Boolean processSyncRequest() throws Exception {
                    return true;
                }
            };
        }
    }

    private static class MyFailedAutoCloseable extends MyAutoCloseable {

        MyFailedAutoCloseable(Plant plant) throws Exception {
            super(plant);
        }

        /* (non-Javadoc)
         * @see java.lang.AutoCloseable#close()
         */
        @Override
        public void close() throws Exception {
            super.close();
            throw new IllegalStateException("FAIL!");
        }
    }

    @Override
    protected void setUp() throws Exception {
        CloseableSet.disableCloseErrorLogging();
    }

    @Override
    protected void tearDown() throws Exception {
        CloseableSet.enableCloseErrorLogging();
    }

    public void testSet() throws Exception {
        final Plant plant = new Plant();
        try {
            final CloseableSet set = new CloseableSet();
            final MyAutoCloseable mac1 = new MyAutoCloseable(plant);
            final MyAutoCloseable mac2 = new MyAutoCloseable(plant);
            final MyAutoCloseable mac3 = new MyAutoCloseable(plant);
            final MyAutoCloseable mac4 = new MyAutoCloseable(plant);
            final MyFailedAutoCloseable mfac = new MyFailedAutoCloseable(plant);
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
        } finally {
            // Close it again, just in case ...
            try {
                plant.close();
            } catch (final Throwable t) {
                // NOP
            }
        }
    }

    public void testFacility() throws Exception {
        // a Plant is also a Facility, so I only need to test the Plant ...
        final Plant plant = new Plant();
        try {
            final MyAutoCloseable mac1 = new MyAutoCloseable(plant);
            final MyAutoCloseable mac2 = new MyAutoCloseable(plant);
            final MyAutoCloseable mac3 = new MyAutoCloseable(plant);
            final MyAutoCloseable mac4 = new MyAutoCloseable(plant);
            final MyFailedAutoCloseable mfac = new MyFailedAutoCloseable(plant);
            plant.addCloseableSReq(mac1).signal();
            plant.addCloseableSReq(mac2).signal();
            plant.addCloseableSReq(mac3).signal();
            plant.addCloseableSReq(mac4).signal();
            plant.addCloseableSReq(mfac).signal();
            plant.removeCloseableSReq(mac4).call();

            plant.closeSReq().call();

            assertEquals(mac1.closed, 0);
            assertEquals(mac2.closed, 0);
            assertEquals(mac3.closed, 0);
            assertEquals(mac4.closed, 0);
            assertEquals(mfac.closed, 0);
        } finally {
            // Close it again, just in case ...
            try {
                plant.close();
            } catch (final Throwable t) {
                // NOP
            }
        }
    }

    public void testReactor() throws Exception {
        final Plant plant = new Plant();
        try {
            final Reactor reactor = new NonBlockingReactor(plant);

            final MyAutoCloseable mac1 = new MyAutoCloseable(plant);
            final MyAutoCloseable mac2 = new MyAutoCloseable(plant);
            final MyAutoCloseable mac3 = new MyAutoCloseable(plant);
            final MyAutoCloseable mac4 = new MyAutoCloseable(plant);
            final MyFailedAutoCloseable mfac = new MyFailedAutoCloseable(plant);
            reactor.addCloseableSReq(mac1).signal();
            reactor.addCloseableSReq(mac2).signal();
            reactor.addCloseableSReq(mac3).signal();
            reactor.addCloseableSReq(mac4).signal();
            reactor.addCloseableSReq(mfac).signal();
            reactor.removeCloseableSReq(mac4).call();

            reactor.closeSReq().call();

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
    }
}
