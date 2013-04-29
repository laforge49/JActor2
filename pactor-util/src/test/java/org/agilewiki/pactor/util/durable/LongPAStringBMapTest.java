package org.agilewiki.pactor.util.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;

public class LongPAStringBMapTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAMap<Long, PAString> m = (PAMap) Durables.
                    newSerializable(mailboxFactory, PAMap.LONG_PASTRING_BMAP);
            assertEquals(0, m.size());
            assertTrue(m.kMake(1L));
            assertFalse(m.kMake(1L));
            assertEquals(1, m.size());
            MapEntry<Long, PAString> me = m.iGet(0);
            assertEquals((Long) 1L, me.getKey());
            PAString v = m.kGet(1L);
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
            mailboxFactory.close();
        }
    }
}
