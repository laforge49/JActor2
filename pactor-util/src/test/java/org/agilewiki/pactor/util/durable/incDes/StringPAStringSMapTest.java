package org.agilewiki.pactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.util.durable.Durables;


public class StringPAStringSMapTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAMap<String, PAString> m = (PAMap) Durables.newSerializable(mailboxFactory, PAMap.STRING_PASTRING_MAP);
            assertNull(m.kGetReq("a").call());
            assertTrue(m.kMakeReq("b").call());
            assertNull(m.kGetReq("a").call());
            PAString value = m.kGetReq("b").call();
            assertNotNull(value);
            MapEntry<String, PAString> entry = m.getHigherReq("a").call();
            assertNotNull(entry);
            assertNull(m.getHigherReq("b").call());
            entry = m.getCeilingReq("b").call();
            assertNotNull(entry);
            assertNull(m.getCeilingReq("c").call());
            assertNull(m.kGetReq("c").call());
            assertTrue(m.kRemoveReq("b").call());
            assertFalse(m.kRemoveReq("b").call());
            assertNull(m.kGetReq("b").call());
        } finally {
            mailboxFactory.close();
        }
    }
}
