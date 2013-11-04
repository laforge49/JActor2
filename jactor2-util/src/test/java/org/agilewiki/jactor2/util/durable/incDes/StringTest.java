package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.util.durable.Durables;

public class StringTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = Durables.createPlant();
        try {
            final JAString jaString1 = (JAString) Durables.newSerializable(
                    plant, JAString.FACTORY_NAME);
            final JAString jaString2 = (JAString) jaString1.copyReq(null)
                    .call();
            jaString2.setValueReq("abc").call();
            final JAString jaString3 = (JAString) jaString2.copyReq(null)
                    .call();

            int sl = jaString1.getSerializedLength();
            assertEquals(4, sl);
            sl = jaString2.getSerializedLength();
            assertEquals(10, sl);
            sl = jaString3.getSerializedLength();
            assertEquals(10, sl);

            assertNull(jaString1.getValueReq().call());
            assertEquals("abc", jaString2.getValueReq().call());
            assertEquals("abc", jaString3.getValueReq().call());

            final Box box = (Box) Durables.newSerializable(plant,
                    Box.FACTORY_NAME);
            box.setValueReq(JAString.FACTORY_NAME).call();
            JAString rpa = (JAString) box.resolvePathnameReq("0").call();
            assertNull(rpa.getValueReq().call());
            assertTrue(rpa.makeValueReq("").call());
            assertFalse(rpa.makeValueReq("Hello?").call());
            rpa = (JAString) box.resolvePathnameReq("0").call();
            assertEquals("", rpa.getValueReq().call());
            rpa.setValueReq("bye").call();
            assertEquals("bye", rpa.getValueReq().call());
            sl = rpa.getSerializedLength();
            assertEquals(10, sl);
            rpa.clearReq().call();
            assertNull(rpa.getValueReq().call());

        } finally {
            plant.close();
        }
    }
}
