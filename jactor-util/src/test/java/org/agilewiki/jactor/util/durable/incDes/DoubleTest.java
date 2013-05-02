package org.agilewiki.jactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.Durables;

public class DoubleTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PADouble double1 = (PADouble) Durables.newSerializable(mailboxFactory, PADouble.FACTORY_NAME);
            PADouble double2 = (PADouble) double1.copy(null);
            double2.setValueReq(1.d).call();
            PADouble double3 = (PADouble) double2.copy(null);

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
            box.setValueReq(PADouble.FACTORY_NAME).call();
            PADouble rpa = (PADouble) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(0.D, v);
            rpa.setValueReq(-1d).call();
            rpa = (PADouble) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(-1.D, v);

        } finally {
            mailboxFactory.close();
        }
    }
}
