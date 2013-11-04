package org.agilewiki.jactor2.util.durable.incDes.timings;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.JAMap;

public class BMapTimingsTest extends TestCase {
    public void test1() throws Exception {

        final int s = 1000;
        final int r = 1000;

        //map size = 1,000
        //repetitions = 1,000
        //total run time (milliseconds) =  41
        //time per update (microseconds) = 41

        //map size = 10,000
        //repetitions = 10,000
        //total run time (milliseconds) = 590
        //time per update (microseconds) = 59

        //map size = 100,000
        //repetitions = 10,000
        //total run time (milliseconds) = 4724
        //time per update (microseconds) = 472

        //map size = 1,000,000
        //repetitions = 1,000
        //total run time (milliseconds) =  9871
        //time per update (microseconds) = 9871

        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m1 = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            int i = 0;
            while (i < s) {
                m1.kMake(i);
                final JAInteger ij0 = m1.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            m1.getSerializedBytes();
            int j = 0;
            i = s / 2;
            final Reactor reactor = new NonBlockingReactor(plant);
            final long t0 = System.currentTimeMillis();
            while (j < r) {
                final JAMap<Integer, JAInteger> m2 = (JAMap) m1.copy(reactor);
                final JAInteger ij0 = m1.kGet(i);
                ij0.setValue(-i);
                m2.getSerializedBytes();
                j += 1;
            }
            final long t1 = System.currentTimeMillis();
            System.out.println("map size = " + s);
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
