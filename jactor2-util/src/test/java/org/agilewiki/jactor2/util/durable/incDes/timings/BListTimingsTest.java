package org.agilewiki.jactor2.util.durable.incDes.timings;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.JAList;

public class BListTimingsTest extends TestCase {
    public void test() throws Exception {

        final int s = 1000;
        final int r = 1000;

        //list size = 1,000
        //repetitions = 1,000
        //total run time (milliseconds) =  63
        //time per update (microseconds) = 63

        //list size = 10,000
        //repetitions = 10,000
        //total run time (milliseconds) = 388
        //time per update (microseconds) = 38

        //list size = 100,000
        //repetitions = 10,000
        //total run time (milliseconds) = 2370
        //time per update (microseconds) = 237

        //list size = 1,000,000
        //repetitions = 1,000
        //total run time (milliseconds) = 2877
        //time per update (microseconds) = 2877

        final Plant plant = Durables.createPlant();
        try {
            final JAList<JAInteger> intList1 = (JAList) Durables
                    .newSerializable(plant, JAList.JAINTEGER_LIST);
            final Reactor reactor = new NonBlockingReactor(plant);
            int i = 0;
            while (i < s) {
                intList1.iAdd(-1);
                final JAInteger ij0 = intList1.iGet(-1);
                ij0.setValue(i);
                i += 1;
            }
            intList1.getSerializedBytes();
            final long t0 = System.currentTimeMillis();
            int j = 0;
            while (j < r) {
                final JAList<JAInteger> intList2 = (JAList) intList1
                        .copy(reactor);
                intList1.iAdd(s / 2);
                intList2.getSerializedBytes();
                j += 1;
            }
            final long t1 = System.currentTimeMillis();
            System.out.println("list size = " + s);
            System.out.println("repetitions = " + r);
            final long rt = t1 - t0;
            System.out.println("total run time (milliseconds) = " + rt);
            final long tpu = (rt * 1000L) / r;
            System.out.println("time per update (microseconds) = " + tpu);
        } finally {
            plant.close();
        }
    }
}
