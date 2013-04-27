package org.agilewiki.pactor.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.MailboxFactory;

public class StringTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            PAString paString1 = (PAString) Durables.newSerializable(mailboxFactory, PAString.FACTORY_NAME);
            PAString paString2 = (PAString) paString1.copyReq(null).call();
            paString2.setStringReq("abc").call();
            PAString paString3 = (PAString) paString2.copyReq(null).call();

            int sl = paString1.getSerializedLength();
            assertEquals(4, sl);
            sl = paString2.getSerializedLength();
            assertEquals(10, sl);
            sl = paString3.getSerializedLength();
            assertEquals(10, sl);

            assertNull(paString1.getStringReq().call());
            assertEquals("abc", paString2.getStringReq().call());
            assertEquals("abc", paString3.getStringReq().call());

            Box box = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box.setIncDesReq(PAString.FACTORY_NAME).call();
            PAString rpa = (PAString) box.resolvePathnameReq("0").call();
            assertNull(rpa.getStringReq().call());
            assertTrue(rpa.makeStringReq("").call());
            assertFalse(rpa.makeStringReq("Hello?").call());
            rpa = (PAString) (PAString) box.resolvePathnameReq("0").call();
            assertEquals("", rpa.getStringReq().call());
            rpa.setStringReq("bye").call();
            assertEquals("bye", rpa.getStringReq().call());
            sl = rpa.getSerializedLength();
            assertEquals(10, sl);
            rpa.clearReq().call();
            assertNull(rpa.getStringReq().call());

        } finally {
            mailboxFactory.close();
        }
    }
}
