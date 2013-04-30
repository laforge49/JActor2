package org.agilewiki.pactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.util.durable.Durables;

public class BooleanTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PABoolean boolean1 = (PABoolean) Durables.newSerializable(mailboxFactory, PABoolean.FACTORY_NAME);
            PABoolean boolean2 = (PABoolean) boolean1.copyReq(null).call();
            boolean2.setValueReq(true).call();
            PABoolean boolean3 = (PABoolean) boolean2.copyReq(null).call();

            int sl = boolean1.getSerializedLength();
            assertEquals(1, sl);
            sl = boolean2.getSerializedLength();
            assertEquals(1, sl);
            sl = boolean3.getSerializedLength();
            assertEquals(1, sl);

            assertFalse(boolean1.getValueReq().call());
            assertTrue(boolean2.getValueReq().call());
            assertTrue(boolean3.getValueReq().call());

            Box box = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box.setValueReq(PABoolean.FACTORY_NAME).call();
            PABoolean rpa = (PABoolean) box.resolvePathnameReq("0").call();
            assertFalse(rpa.getValueReq().call());
            rpa.setValueReq(true).call();
            rpa = (PABoolean) box.resolvePathnameReq("0").call();
            assertTrue(rpa.getValueReq().call());

        } finally {
            mailboxFactory.close();
        }
    }
}
