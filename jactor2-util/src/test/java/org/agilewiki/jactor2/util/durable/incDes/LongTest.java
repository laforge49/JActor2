package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.util.durable.Durables;

public class LongTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = Durables.createPlant();
        try {
            final JALong long1 = (JALong) Durables.newSerializable(plant,
                    JALong.FACTORY_NAME);
            final JALong long2 = (JALong) long1.copyReq(null).call();
            long2.setValueReq(1L).call();
            final JALong long3 = (JALong) long2.copyReq(null).call();

            int sl = long1.getSerializedLength();
            assertEquals(8, sl);
            sl = long2.getSerializedLength();
            assertEquals(8, sl);
            sl = long3.getSerializedLength();
            assertEquals(8, sl);

            long v = long1.getValueReq().call();
            assertEquals(0L, v);
            v = long2.getValueReq().call();
            assertEquals(1L, v);
            v = long3.getValueReq().call();
            assertEquals(1L, v);

            final Box box = (Box) Durables.newSerializable(plant,
                    Box.FACTORY_NAME);
            box.setValueReq(JALong.FACTORY_NAME).call();
            JALong rpa = (JALong) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(0L, v);
            rpa.setValueReq(-1000000000000L).call();
            rpa = (JALong) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(-1000000000000L, v);

        } finally {
            plant.close();
        }
    }
}
