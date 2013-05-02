package org.agilewiki.jactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.Durables;

public class LongTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PALong long1 = (PALong) Durables.newSerializable(mailboxFactory, PALong.FACTORY_NAME);
            PALong long2 = (PALong) long1.copyReq(null).call();
            long2.setValueReq(1L).call();
            PALong long3 = (PALong) long2.copyReq(null).call();

            int sl = long1.getSerializedLength();
            assertEquals(8, sl);
            sl = long2.getSerializedLength();
            assertEquals(8, sl);
            sl = long3.getSerializedLength();
            assertEquals(8, sl);

            long v = long1.getValueReq().call();
            assertEquals(0L, v);
            v = long2.getValueReq().call();
            assertEquals(1L, v);
            v = long3.getValueReq().call();
            assertEquals(1L, v);

            Box box = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box.setValueReq(PALong.FACTORY_NAME).call();
            PALong rpa = (PALong) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(0L, v);
            rpa.setValueReq(-1000000000000L).call();
            rpa = (PALong) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(-1000000000000L, v);

        } finally {
            mailboxFactory.close();
        }
    }
}
