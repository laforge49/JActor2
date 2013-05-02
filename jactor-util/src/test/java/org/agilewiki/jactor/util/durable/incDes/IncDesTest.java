package org.agilewiki.jactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.Durables;

public class IncDesTest extends TestCase {
    public void test1() throws Exception {
        System.err.println("\nTest 1");
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            IncDes a = (IncDes) Durables.newSerializable(mailboxFactory, IncDes.FACTORY_NAME);
            int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            mailboxFactory.close();
        }
    }

    public void test4() throws Exception {
        System.err.println("\nTest 4");
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            IncDes a = (IncDes) Durables.newSerializable(mailboxFactory, IncDes.FACTORY_NAME);
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
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            IncDes a = (IncDes) Durables.newSerializable(mailboxFactory, IncDes.FACTORY_NAME);
            a.load(new byte[0]);
            int l = a.getSerializedLengthReq().call();
            System.err.println(l);
            assertEquals(l, 0);
        } finally {
            mailboxFactory.close();
        }
    }

    public void test6() throws Exception {
        System.err.println("\nTest 6");
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            IncDes jid1 = (IncDes) Durables.newSerializable(mailboxFactory, IncDes.FACTORY_NAME);
            jid1.load(new byte[0]);
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
