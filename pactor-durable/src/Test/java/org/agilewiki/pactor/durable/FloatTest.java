package org.agilewiki.pactor.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;

public class FloatTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            PAFloat float1 = (PAFloat) Durables.newSerializable(mailboxFactory, PAFloat.FACTORY_NAME);
            PAFloat float2 = (PAFloat) float1.copyReq(null).call();
            float2.setFloatReq(1.0f).call();
            PAFloat float3 = (PAFloat) float2.copyReq(null).call();

            int sl = float1.getSerializedLength();
            assertEquals(4, sl);
            sl = float2.getSerializedLength();
            assertEquals(4, sl);
            sl = float3.getSerializedLength();
            assertEquals(4, sl);

            float v = float1.getFloatReq().call();
            assertEquals(0.f, v);
            v = float2.getFloatReq().call();
            assertEquals(1.f, v);
            v = float3.getFloatReq().call();
            assertEquals(1.f, v);

            Box box = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box.setIncDesReq(PAFloat.FACTORY_NAME).call();
            PAFloat rpa = (PAFloat) box.resolvePathnameReq("0").call();
            v = rpa.getFloatReq().call();
            assertEquals(0.f, v);
            rpa.setFloatReq(-1.0f).call();
            rpa = (PAFloat) box.resolvePathnameReq("0").call();
            v = rpa.getFloatReq().call();
            assertEquals(-1.f, v);

        } finally {
            mailboxFactory.close();
        }
    }
}
