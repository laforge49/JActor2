package org.agilewiki.pactor.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.MailboxFactory;

public class BooleanTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            PABoolean boolean1 = (PABoolean) Durables.newSerializable(mailboxFactory, PABoolean.FACTORY_NAME);
            PABoolean boolean2 = (PABoolean) boolean1.copyReq(null).call();
            boolean2.setBooleanReq(true).call();
            PABoolean boolean3 = (PABoolean) boolean2.copyReq(null).call();

            int sl = boolean1.getSerializedLength();
            assertEquals(1, sl);
            sl = boolean2.getSerializedLength();
            assertEquals(1, sl);
            sl = boolean3.getSerializedLength();
            assertEquals(1, sl);

            assertFalse(boolean1.getBooleanReq().call());
            assertTrue(boolean2.getBooleanReq().call());
            assertTrue(boolean3.getBooleanReq().call());

            Box box = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box.setIncDesReq(PABoolean.FACTORY_NAME).call();
            PABoolean rpa = (PABoolean) box.resolvePathnameReq("0").call();
            assertFalse(rpa.getBooleanReq().call());
            rpa.setBooleanReq(true).call();
            rpa = (PABoolean) box.resolvePathnameReq("0").call();
            assertTrue(rpa.getBooleanReq().call());

        } finally {
            mailboxFactory.close();
        }
    }
}
