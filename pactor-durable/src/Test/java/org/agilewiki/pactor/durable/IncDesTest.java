package org.agilewiki.pactor.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

public class IncDesTest extends TestCase {
    public void test1() throws Exception {
        System.err.println("\nTest 1");
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            IncDes a = (IncDes) Util.newSerializable(mailboxFactory, IncDes.FACTORY_NAME);
            int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            mailboxFactory.close();
        }
    }

    public void test3() throws Exception {
        System.err.println("\nTest 3");
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            IncDes a = (IncDes) Util.newSerializable(mailboxFactory, IncDes.FACTORY_NAME);
            int l = a.getSerializedLengthReq().call();
            AppendableBytes appendableBytes = new AppendableBytes(l);
            a.save(appendableBytes);
        } finally {
            mailboxFactory.close();
        }
    }

    public void test4() throws Exception {
        System.err.println("\nTest 4");
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            IncDes a = (IncDes) Util.newSerializable(mailboxFactory, IncDes.FACTORY_NAME);
            byte[] bytes = a.getSerializedBytesReq().call();
            int l = bytes.length;
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            mailboxFactory.close();
        }
    }

    public void test5() throws Exception {
        System.err.println("\nTest 5");
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            IncDes a = (IncDes) Util.newSerializable(mailboxFactory, IncDes.FACTORY_NAME);
            a.load(new ReadableBytes(new byte[0], 0));
            int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            mailboxFactory.close();
        }
    }

    public void test6() throws Exception {
        System.err.println("\nTest 6");
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            IncDes jid1 = (IncDes) Util.newSerializable(mailboxFactory, IncDes.FACTORY_NAME);
            jid1.load(new ReadableBytes(new byte[0], 0));
            Mailbox mailbox = mailboxFactory.createMailbox();
            IncDes jid2 = (IncDes) jid1.copyReq(mailbox).call();
            int l = jid2.getDurable().getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
            boolean eq = jid1.isEqualReq(jid2).call();
            assertTrue(eq);
        } finally {
            mailboxFactory.close();
        }
    }
}
