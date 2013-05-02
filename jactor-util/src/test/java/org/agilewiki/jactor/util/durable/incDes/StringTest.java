package org.agilewiki.jactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.Durables;

public class StringTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            JAString paString1 = (JAString) Durables.newSerializable(mailboxFactory, JAString.FACTORY_NAME);
            JAString paString2 = (JAString) paString1.copyReq(null).call();
            paString2.setValueReq("abc").call();
            JAString paString3 = (JAString) paString2.copyReq(null).call();

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
            box.setValueReq(JAString.FACTORY_NAME).call();
            JAString rpa = (JAString) box.resolvePathnameReq("0").call();
            assertNull(rpa.getValueReq().call());
            assertTrue(rpa.makeValueReq("").call());
            assertFalse(rpa.makeValueReq("Hello?").call());
            rpa = (JAString) (JAString) box.resolvePathnameReq("0").call();
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
