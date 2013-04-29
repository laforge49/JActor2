package org.agilewiki.pactor.util.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;

public class StringPAStringBMapTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAMap<String, PAString> m = (PAMap) Durables.
                    newSerializable(mailboxFactory, PAMap.STRING_PASTRING_BMAP);
            assertEquals(0, m.size());
            assertTrue(m.kMake("1"));
            assertFalse(m.kMake("1"));
            assertEquals(1, m.size());
            MapEntry<String, PAString> me = m.iGet(0);
            assertEquals("1", me.getKey());
            PAString v = m.kGet("1");
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
            mailboxFactory.close();
        }
    }
}
