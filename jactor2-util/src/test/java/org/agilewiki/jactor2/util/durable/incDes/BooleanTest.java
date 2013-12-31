package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.util.durable.Durables;

public class BooleanTest extends TestCase {
    public void test() throws Exception {
        final BasicPlant plant = Durables.createPlant();
        try {
            final JABoolean boolean1 = (JABoolean) Durables.newSerializable(
                    plant, JABoolean.FACTORY_NAME);
            final JABoolean boolean2 = (JABoolean) boolean1.copyReq(null)
                    .call();
            boolean2.setValueReq(true).call();
            final JABoolean boolean3 = (JABoolean) boolean2.copyReq(null)
                    .call();

            int sl = boolean1.getSerializedLength();
            assertEquals(1, sl);
            sl = boolean2.getSerializedLength();
            assertEquals(1, sl);
            sl = boolean3.getSerializedLength();
            assertEquals(1, sl);

            assertFalse(boolean1.getValueReq().call());
            assertTrue(boolean2.getValueReq().call());
            assertTrue(boolean3.getValueReq().call());

            final Box box = (Box) Durables.newSerializable(plant,
                    Box.FACTORY_NAME);
            box.setValueReq(JABoolean.FACTORY_NAME).call();
            JABoolean rpa = (JABoolean) box.resolvePathnameReq("0").call();
            assertFalse(rpa.getValueReq().call());
            rpa.setValueReq(true).call();
            rpa = (JABoolean) box.resolvePathnameReq("0").call();
            assertTrue(rpa.getValueReq().call());

        } finally {
            plant.close();
        }
    }
}
