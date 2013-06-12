package org.agilewiki.jactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor.util.UtilMailboxFactory;
import org.agilewiki.jactor.util.durable.Durables;

public class FloatTest extends TestCase {
    public void test() throws Exception {
        UtilMailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            JAFloat float1 = (JAFloat) Durables.newSerializable(mailboxFactory, JAFloat.FACTORY_NAME);
            JAFloat float2 = (JAFloat) float1.copyReq(null).call();
            float2.setValueReq(1.0f).call();
            JAFloat float3 = (JAFloat) float2.copyReq(null).call();

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
            box.setValueReq(JAFloat.FACTORY_NAME).call();
            JAFloat rpa = (JAFloat) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(0.f, v);
            rpa.setValueReq(-1.0f).call();
            rpa = (JAFloat) box.resolvePathnameReq("0").call();
            v = rpa.getValueReq().call();
            assertEquals(-1.f, v);

        } finally {
            mailboxFactory.close();
        }
    }
}
