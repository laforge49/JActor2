package org.agilewiki.jactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.Durables;

public class BListTest extends TestCase {
    public void test1() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAList<PAString> stringList1 = (PAList) Durables.newSerializable(mailboxFactory, PAList.PASTRING_LIST);
            stringList1.iAdd(0);
            stringList1.iAdd(1);
            stringList1.iAdd(2);
            PAString sj0 = stringList1.iGet(0);
            PAString sj1 = stringList1.iGet(1);
            PAString sj2 = stringList1.iGet(2);
            sj0.setValue("a");
            sj1.setValue("b");
            sj2.setValue("c");
            Mailbox mailbox = mailboxFactory.createMailbox();
            PAList<PAString> stringList2 = (PAList) stringList1.copy(mailbox);
            PAString s0 = stringList2.iGet(0);
            PAString s1 = stringList2.iGet(1);
            PAString s2 = stringList2.iGet(2);
            assertEquals("a", s0.getValue());
            assertEquals("b", s1.getValue());
            assertEquals("c", s2.getValue());
        } finally {
            mailboxFactory.close();
        }
    }

    public void test2() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAList<PAInteger> intList1 = (PAList) Durables.newSerializable(mailboxFactory, PAList.PAINTEGER_LIST);
            int i = 0;
            while (i < 28) {
                intList1.iAdd(i);
                PAInteger ij0 = intList1.iGet(i);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 28) {
                PAInteger ij = intList1.iGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            mailboxFactory.close();
        }
    }

    public void test3() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAList<PAInteger> intList1 = (PAList) Durables.newSerializable(mailboxFactory, PAList.PAINTEGER_LIST);
            int i = 0;
            while (i < 41) {
                intList1.iAdd(-1);
                PAInteger ij0 = intList1.iGet(-1);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 41) {
                PAInteger ij = intList1.iGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            mailboxFactory.close();
        }
    }

    public void test4() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAList<PAInteger> intList1 = (PAList) Durables.newSerializable(mailboxFactory, PAList.PAINTEGER_LIST);
            int i = 0;
            while (i < 391) {
                intList1.iAdd(-1);
                PAInteger ij0 = intList1.iGet(-1);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 391) {
                PAInteger ij = intList1.iGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            mailboxFactory.close();
        }
    }

    public void test5() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAList<PAInteger> intList1 = (PAList) Durables.newSerializable(mailboxFactory, PAList.PAINTEGER_LIST);
            int i = 0;
            while (i < 10000) {
                intList1.iAdd(-1);
                PAInteger ij0 = intList1.iGet(-1);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 10000) {
                PAInteger ij = intList1.iGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            mailboxFactory.close();
        }
    }

    public void test6() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            PAList<PAInteger> intList1 = (PAList) Durables.newSerializable(mailboxFactory, PAList.PAINTEGER_LIST);
            int i = 0;
            while (i < 10000) {
                intList1.iAdd(-1);
                PAInteger ij0 = intList1.iGet(-1);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 10000) {
                intList1.iRemove(-1);
                i += 1;
            }
            assertEquals(0, intList1.size());
        } finally {
            mailboxFactory.close();
        }
    }
}
