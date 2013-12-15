package org.agilewiki.jactor2.core.util;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Test code.
 */
public class CloseableSetTest extends TestCase {
    public void testSet() throws Exception {
        System.out.println("S");
        final Plant plant = new Plant();
        try {
            final Set<Closeable> set = Collections.newSetFromMap(new WeakHashMap<Closeable, Boolean>());
            final MyCloseable mac1 = new MyCloseable(plant);
            final MyCloseable mac2 = new MyCloseable(plant);
            final MyCloseable mac3 = new MyCloseable(plant);
            final MyCloseable mac4 = new MyCloseable(plant);
            final MyFailedCloseable mfac = new MyFailedCloseable(plant);
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
            for (final Closeable ac : set) {
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

            final Closeable[] array = set.toArray(
                    new Closeable[set.size()]);
            for (final Closeable ac : array) {
                try {
                    ac.close();
                } catch (final Throwable t) {
                    //System.out.println("Error closing a " + ac.getClass().getName()+" "+t);
                }
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
    }

    public void testFacility() throws Exception {
        System.out.println("F");
        // a Plant is also a Facility, so I only need to test the Plant ...
        final Plant plant = new Plant();
        try {
            final MyCloseable mac1 = new MyCloseable(plant);
            final MyCloseable mac2 = new MyCloseable(plant);
            final MyCloseable mac3 = new MyCloseable(plant);
            final MyCloseable mac4 = new MyCloseable(plant);
            final MyFailedCloseable mfac = new MyFailedCloseable(plant);
            Facility facility = plant.facility();
            facility.addCloseable(mac1);
            facility.addCloseable(mac2);
            facility.addCloseable(mac3);
            facility.addCloseable(mac4);
            facility.addCloseable(mfac);
            facility.removeCloseable(mac4);

            System.out.println("first plant.close");
            plant.close();

            assertEquals(mac1.closed, 1);
            assertEquals(mac2.closed, 1);
            assertEquals(mac3.closed, 1);
            assertEquals(mac4.closed, 0);
            assertEquals(mfac.closed, 1);
        } finally {
            // Close it again, just in case ...
            try {
                System.out.println("second plant.close");
                plant.close();
            } catch (final Throwable t) {
                // NOP
            }
        }
    }

    public void testReactor() throws Exception {
        System.out.println("R");
        final Plant plant = new Plant();
        try {
            final Reactor reactor = new NonBlockingReactor(plant);

            final MyCloseable mac1 = new MyCloseable(plant);
            final MyCloseable mac2 = new MyCloseable(plant);
            final MyCloseable mac3 = new MyCloseable(plant);
            final MyCloseable mac4 = new MyCloseable(plant);
            final MyFailedCloseable mfac = new MyFailedCloseable(plant);
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

class MyCloseable extends CloseableBase {
    public volatile int closed;

    MyCloseable(Plant plant) throws Exception {
        initialize(new NonBlockingReactor(plant));
    }

    @Override
    public void close() throws Exception {
        closed++;
        super.close();
    }
}

class MyFailedCloseable extends MyCloseable {

    MyFailedCloseable(Plant plant) throws Exception {
        super(plant);
    }

    @Override
    public void close() throws Exception {
        super.close();
        throw new IllegalStateException("FAIL!");
    }
}
