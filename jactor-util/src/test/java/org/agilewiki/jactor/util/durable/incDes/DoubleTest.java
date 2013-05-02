package org.agilewiki.jactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.Durables;

public class DoubleTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            JADouble double1 = (JADouble) Durables.newSerializable(mailboxFactory, JADouble.FACTORY_NAME);
            JADouble double2 = (JADouble) double1.copy(null);
            double2.setValueReq(1.d).call();
            JADouble double3 = (JADouble) double2.copy(null);

            int sl = double1.getSerializedLength();
            assertEquals(8, sl);
            sl = double2.getSerializedLength();
            assertEquals(8, sl);
            sl = double3.getSerializedLength();
            assertEquals(8, sl);

            double v = double1.getValueReq().call();
            assertEquals(0.D, v);
            v = double2.getValueReq().call();
            assertEquals(1.D, v);
            v = double3.getValueReq().call();
            assertEquals(1.D, v);

            Box box = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box.setValueReq(JADouble.FACTORY_NAME).call();
            JADouble rpa = (JADouble) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(0.D, v);
            rpa.setValueReq(-1d).call();
            rpa = (JADouble) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(-1.D, v);

        } finally {
            mailboxFactory.close();
        }
    }
}
