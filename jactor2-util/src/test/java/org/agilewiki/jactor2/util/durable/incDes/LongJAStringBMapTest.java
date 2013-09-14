package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.util.durable.Durables;

public class LongJAStringBMapTest extends TestCase {
    public void test() throws Exception {
        Facility facility = Durables.createFacility();
        try {
            JAMap<Long, JAString> m = (JAMap) Durables.
                    newSerializable(facility, JAMap.LONG_JASTRING_MAP);
            assertEquals(0, m.size());
            assertTrue(m.kMake(1L));
            assertFalse(m.kMake(1L));
            assertEquals(1, m.size());
            MapEntry<Long, JAString> me = m.iGet(0);
            assertEquals((Long) 1L, me.getKey());
            JAString v = m.kGet(1L);
            assertEquals(v, me.getValue());
            assertEquals(me, m.getCeiling(0L));
            assertEquals(me, m.getCeiling(1L));
            assertNull(m.getCeiling(2L));
            assertEquals(me, m.getHigher(0L));
            assertNull(m.getHigher(1L));
            m.empty();
            assertEquals(0, m.size());
            assertTrue(m.kMake(1L));
            assertEquals(1, m.size());
            me = m.iGet(0);
            assertEquals((Long) 1L, me.getKey());
            m.iRemove(0);
            assertEquals(0, m.size());
            assertTrue(m.kMake(1L));
            assertEquals(1, m.size());
            me = m.iGet(0);
            assertEquals((Long) 1L, me.getKey());
            assertFalse(m.kRemove(0L));
            assertTrue(m.kRemove(1L));
            assertEquals(0, m.size());
        } finally {
            facility.close();
        }
    }
}
