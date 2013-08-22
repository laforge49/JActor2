package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.util.durable.Durables;

public class BytesTest extends TestCase {
    public void test() throws Exception {
        ModuleContext moduleContext = Durables.createModuleContext();
        try {
            Bytes bytes1 = (Bytes) Durables.newSerializable(moduleContext, Bytes.FACTORY_NAME);
            Bytes bytes2 = (Bytes) bytes1.copyReq(null).call();
            bytes2.setValueReq(new byte[3]).call();
            Bytes bytes3 = (Bytes) bytes2.copyReq(null).call();

            int sl = bytes1.getSerializedLength();
            assertEquals(4, sl);
            sl = bytes2.getSerializedLength();
            assertEquals(7, sl);
            sl = bytes3.getSerializedLength();
            assertEquals(7, sl);

            assertNull(bytes1.getValueReq().call());
            assertEquals(3, bytes2.getValueReq().call().length);
            assertEquals(3, bytes3.getValueReq().call().length);

            Box box = (Box) Durables.newSerializable(moduleContext, Box.FACTORY_NAME);
            box.setValueReq(Bytes.FACTORY_NAME).call();
            Bytes rpa = (Bytes) box.resolvePathnameReq("0").call();
            assertNull(rpa.getValueReq().call());
            assertTrue(rpa.makeValueReq(new byte[0]).call());
            assertFalse(rpa.makeValueReq(new byte[99]).call());
            rpa = (Bytes) box.resolvePathnameReq("0").call();
            assertEquals(0, rpa.getValueReq().call().length);
            rpa.clearReq().call();
            assertNull(rpa.getValueReq().call());

        } finally {
            moduleContext.close();
        }
    }
}
