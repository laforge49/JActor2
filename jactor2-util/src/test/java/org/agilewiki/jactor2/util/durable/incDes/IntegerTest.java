package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.util.durable.Durables;

public class IntegerTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = Durables.createPlant();
        try {
            final JAInteger int1 = (JAInteger) Durables.newSerializable(plant,
                    JAInteger.FACTORY_NAME);
            final JAInteger int2 = (JAInteger) int1.copyReq(null).call();
            int2.setValueReq(1).call();
            final JAInteger int3 = (JAInteger) int2.copyReq(null).call();

            int sl = int1.getSerializedLength();
            assertEquals(4, sl);
            sl = int2.getSerializedLength();
            assertEquals(4, sl);
            sl = int3.getSerializedLength();
            assertEquals(4, sl);

            int v = int1.getValueReq().call();
            assertEquals(0, v);
            v = int2.getValueReq().call();
            assertEquals(1, v);
            v = int3.getValueReq().call();
            assertEquals(1, v);

            final Box box1 = (Box) Durables.newSerializable(plant,
                    Box.FACTORY_NAME);
            box1.setValueReq(JAInteger.FACTORY_NAME).call();
            JAInteger rpa = (JAInteger) box1.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(0, v);
            rpa.setValueReq(-1).call();
            rpa = (JAInteger) box1.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(-1, v);

        } finally {
            plant.close();
        }
    }
}
