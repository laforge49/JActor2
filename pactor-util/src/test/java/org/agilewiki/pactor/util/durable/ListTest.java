package org.agilewiki.pactor.util.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;

public class ListTest extends TestCase {
    public void test1() throws Exception {
        t(PAList.PASTRING_LIST, 8, 12, 20, 24, 16);
    }

    /*
    public void test2() throws Exception {
        t(IncDesFactories.PASTRING_BLIST, 20, 24, 32, 36, 28);
    }
    */

    void t(String lt, int elsl, int nslsl, int sls, int l2sl2, int l2sl3) throws Exception {
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            PAList<PAString> l0 = (PAList) Durables.newSerializable(mailboxFactory, lt);
            int l0sl = l0.getSerializedLength();
            assertEquals(elsl, l0sl);
            PAList<PAString> l1 = (PAList) l0.copyReq(null).call();
            int l1sl = l1.getSerializedLength();
            assertEquals(elsl, l1sl);
            l1.iAddReq(0).call();
            l1sl = l1.getSerializedLength();
            assertEquals(nslsl, l1sl);
            PAList<PAString> l2 = (PAList) l1.copyReq(null).call();
            int l2sl = l2.getSerializedLength();
            assertEquals(nslsl, l2sl);
            PAString s0 = (PAString) Durables.newSerializable(mailboxFactory, PAString.FACTORY_NAME);
            s0.setStringReq("Hi").call();
            int s0sl = s0.getSerializedLength();
            assertEquals(8, s0sl);
            byte[] s0bs = s0.getSerializedBytes();
            assertEquals(8, s0bs.length);
            l2.iAddReq(-1, s0bs).call();
            l2sl = l2.getSerializedLength();
            assertEquals(sls, l2sl);
            l2.iSetReq(0, s0bs).call();
            l2sl = l2.getSerializedLength();
            assertEquals(l2sl2, l2sl);
            l2.iRemoveReq(0).call();
            l2sl = l2.getSerializedLength();
            assertEquals(l2sl3, l2sl);
            l2.emptyReq().call();
            l2sl = l2.getSerializedLength();
            assertEquals(elsl, l2sl);
        } finally {
            mailboxFactory.close();
        }
    }
}
