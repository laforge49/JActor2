package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.util.durable.Durables;

public class StringJAStringBMapTest extends TestCase {
    public void test() throws Exception {
        final BasicPlant plant = Durables.createPlant();
        try {
            final JAMap<String, JAString> m = (JAMap) Durables.newSerializable(
                    plant, JAMap.STRING_JASTRING_MAP);
            assertEquals(0, m.size());
            assertTrue(m.kMake("1"));
            assertFalse(m.kMake("1"));
            assertEquals(1, m.size());
            MapEntry<String, JAString> me = m.iGet(0);
            assertEquals("1", me.getKey());
            final JAString v = m.kGet("1");
            assertEquals(v, me.getValue());
            assertEquals(me, m.getCeiling("0"));
            assertEquals(me, m.getCeiling("1"));
            assertNull(m.getCeiling("2"));
            assertEquals(me, m.getHigher("0"));
            assertNull(m.getHigher("1"));
            m.empty();
            assertEquals(0, m.size());
            assertTrue(m.kMake("1"));
            assertEquals(1, m.size());
            me = m.iGet(0);
            assertEquals("1", me.getKey());
            m.iRemove(0);
            assertEquals(0, m.size());
            assertTrue(m.kMake("1"));
            assertEquals(1, m.size());
            me = m.iGet(0);
            assertEquals("1", me.getKey());
            assertFalse(m.kRemove("0"));
            assertTrue(m.kRemove("1"));
            assertEquals(0, m.size());
        } finally {
            plant.close();
        }
    }
}
