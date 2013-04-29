package org.agilewiki.pactor.util.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.api.MailboxFactory;

public class TupleTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            FactoryLocator factoryLocator = Durables.getFactoryLocator(mailboxFactory);
            Durables.registerTupleFactory(factoryLocator,
                    "sst", PAString.FACTORY_NAME, PAString.FACTORY_NAME);
            Factory tjf = factoryLocator.getFactory("sst");
            Mailbox mailbox = mailboxFactory.createMailbox();
            Tuple t0 = (Tuple) tjf.newSerializable(mailbox, factoryLocator);
            PAString e0 = (PAString) t0.iGetReq(0).call();
            assertNull(e0.getValueReq().call());
            PAString e1 = (PAString) t0.iGetReq(1).call();
            assertNull(e1.getValueReq().call());
            e0.setValueReq("Apples").call();
            assertEquals("Apples", e0.getValueReq().call());
            e1.setValueReq("Oranges").call();
            assertEquals("Oranges", e1.getValueReq().call());
            Tuple t1 = (Tuple) t0.copyReq(null).call();
            PAString f0 = (PAString) t1.resolvePathnameReq("0").call();
            assertEquals("Apples", f0.getValueReq().call());
            PAString f1 = (PAString) t1.resolvePathnameReq("1").call();
            assertEquals("Oranges", f1.getValueReq().call());

            PAString paString1 = (PAString) Durables.newSerializable(mailboxFactory, PAString.FACTORY_NAME);
            paString1.setValueReq("Peaches").call();
            byte[] sb = paString1.getSerializedBytesReq().call();
            t1.iSetReq(1, sb).call();
            PAString f1b = (PAString) t1.resolvePathnameReq("1").call();
            assertEquals("Peaches", f1b.getValueReq().call());
        } finally {
            mailboxFactory.close();
        }
    }
}
