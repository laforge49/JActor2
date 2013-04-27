package org.agilewiki.pactor.util.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;

public class IntegerTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            PAInteger int1 = (PAInteger) Durables.newSerializable(mailboxFactory, PAInteger.FACTORY_NAME);
            PAInteger int2 = (PAInteger) int1.copyReq(null).call();
            int2.setIntegerReq(1).call();
            PAInteger int3 = (PAInteger) int2.copyReq(null).call();

            int sl = int1.getSerializedLength();
            assertEquals(4, sl);
            sl = int2.getSerializedLength();
            assertEquals(4, sl);
            sl = int3.getSerializedLength();
            assertEquals(4, sl);

            int v = int1.getIntegerReq().call();
            assertEquals(0, v);
            v = int2.getIntegerReq().call();
            assertEquals(1, v);
            v = int3.getIntegerReq().call();
            assertEquals(1, v);

            Box box1 = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box1.setIncDesReq(PAInteger.FACTORY_NAME).call();
            PAInteger rpa = (PAInteger) box1.resolvePathnameReq("0").call();
            v = rpa.getIntegerReq().call();
            assertEquals(0, v);
            rpa.setIntegerReq(-1).call();
            rpa = (PAInteger) box1.resolvePathnameReq("0").call();
            v = rpa.getIntegerReq().call();
            assertEquals(-1, v);

        } finally {
            mailboxFactory.close();
        }
    }
}
