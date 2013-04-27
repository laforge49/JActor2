package org.agilewiki.pactor.util.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;

public class BytesTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            Bytes bytes1 = (Bytes) Durables.newSerializable(mailboxFactory, Bytes.FACTORY_NAME);
            Bytes bytes2 = (Bytes) bytes1.copyReq(null).call();
            bytes2.setBytesReq(new byte[3]).call();
            Bytes bytes3 = (Bytes) bytes2.copyReq(null).call();

            int sl = bytes1.getSerializedLength();
            assertEquals(4, sl);
            sl = bytes2.getSerializedLength();
            assertEquals(7, sl);
            sl = bytes3.getSerializedLength();
            assertEquals(7, sl);

            assertNull(bytes1.getBytesReq().call());
            assertEquals(3, bytes2.getBytesReq().call().length);
            assertEquals(3, bytes3.getBytesReq().call().length);

            Box box = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box.setIncDesReq(Bytes.FACTORY_NAME).call();
            Bytes rpa = (Bytes) box.resolvePathnameReq("0").call();
            assertNull(rpa.getBytesReq().call());
            assertTrue(rpa.makeBytesReq(new byte[0]).call());
            assertFalse(rpa.makeBytesReq(new byte[99]).call());
            rpa = (Bytes) box.resolvePathnameReq("0").call();
            assertEquals(0, rpa.getBytesReq().call().length);
            rpa.clearReq().call();
            assertNull(rpa.getBytesReq().call());

        } finally {
            mailboxFactory.close();
        }
    }
}
