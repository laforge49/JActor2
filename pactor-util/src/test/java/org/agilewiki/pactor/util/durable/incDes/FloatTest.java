package org.agilewiki.pactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.util.durable.Durables;

public class FloatTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAFloat float1 = (PAFloat) Durables.newSerializable(mailboxFactory, PAFloat.FACTORY_NAME);
            PAFloat float2 = (PAFloat) float1.copyReq(null).call();
            float2.setValueReq(1.0f).call();
            PAFloat float3 = (PAFloat) float2.copyReq(null).call();

            int sl = float1.getSerializedLength();
            assertEquals(4, sl);
            sl = float2.getSerializedLength();
            assertEquals(4, sl);
            sl = float3.getSerializedLength();
            assertEquals(4, sl);

            float v = float1.getValueReq().call();
            assertEquals(0.f, v);
            v = float2.getValueReq().call();
            assertEquals(1.f, v);
            v = float3.getValueReq().call();
            assertEquals(1.f, v);

            Box box = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box.setValueReq(PAFloat.FACTORY_NAME).call();
            PAFloat rpa = (PAFloat) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(0.f, v);
            rpa.setValueReq(-1.0f).call();
            rpa = (PAFloat) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(-1.f, v);

        } finally {
            mailboxFactory.close();
        }
    }
}
