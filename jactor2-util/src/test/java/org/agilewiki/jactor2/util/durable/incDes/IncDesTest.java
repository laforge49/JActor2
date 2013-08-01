package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.Mailbox;
import org.agilewiki.jactor2.core.mailbox.NonBlockingMailbox;
import org.agilewiki.jactor2.util.durable.Durables;

public class IncDesTest extends TestCase {
    public void test1() throws Exception {
        System.err.println("\nTest 1");
        JAContext jaContext = Durables.createJAContext();
        try {
            IncDes a = (IncDes) Durables.newSerializable(jaContext, IncDes.FACTORY_NAME);
            int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            jaContext.close();
        }
    }

    public void test4() throws Exception {
        System.err.println("\nTest 4");
        JAContext jaContext = Durables.createJAContext();
        try {
            IncDes a = (IncDes) Durables.newSerializable(jaContext, IncDes.FACTORY_NAME);
            byte[] bytes = a.getSerializedBytesReq().call();
            int l = bytes.length;
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            jaContext.close();
        }
    }

    public void test5() throws Exception {
        System.err.println("\nTest 5");
        JAContext jaContext = Durables.createJAContext();
        try {
            IncDes a = (IncDes) Durables.newSerializable(jaContext, IncDes.FACTORY_NAME);
            a.load(new byte[0]);
            int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            jaContext.close();
        }
    }

    public void test6() throws Exception {
        System.err.println("\nTest 6");
        JAContext jaContext = Durables.createJAContext();
        try {
            IncDes jid1 = (IncDes) Durables.newSerializable(jaContext, IncDes.FACTORY_NAME);
            jid1.load(new byte[0]);
            Mailbox mailbox = new NonBlockingMailbox(jaContext);
            IncDes jid2 = (IncDes) jid1.copyReq(mailbox).call();
            int l = jid2.getDurable().getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
            boolean eq = jid1.isEqualReq(jid2).call();
            assertTrue(eq);
        } finally {
            jaContext.close();
        }
    }
}
