package org.agilewiki.pactor.util.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;

public class StringTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAString paString1 = (PAString) Durables.newSerializable(mailboxFactory, PAString.FACTORY_NAME);
            PAString paString2 = (PAString) paString1.copyReq(null).call();
            paString2.setValueReq("abc").call();
            PAString paString3 = (PAString) paString2.copyReq(null).call();

            int sl = paString1.getSerializedLength();
            assertEquals(4, sl);
            sl = paString2.getSerializedLength();
            assertEquals(10, sl);
            sl = paString3.getSerializedLength();
            assertEquals(10, sl);

            assertNull(paString1.getValueReq().call());
            assertEquals("abc", paString2.getValueReq().call());
            assertEquals("abc", paString3.getValueReq().call());

            Box box = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box.setValueReq(PAString.FACTORY_NAME).call();
            PAString rpa = (PAString) box.resolvePathnameReq("0").call();
            assertNull(rpa.getValueReq().call());
            assertTrue(rpa.makeValueReq("").call());
            assertFalse(rpa.makeValueReq("Hello?").call());
            rpa = (PAString) (PAString) box.resolvePathnameReq("0").call();
            assertEquals("", rpa.getValueReq().call());
            rpa.setValueReq("bye").call();
            assertEquals("bye", rpa.getValueReq().call());
            sl = rpa.getSerializedLength();
            assertEquals(10, sl);
            rpa.clearReq().call();
            assertNull(rpa.getValueReq().call());

        } finally {
            mailboxFactory.close();
        }
    }
}
