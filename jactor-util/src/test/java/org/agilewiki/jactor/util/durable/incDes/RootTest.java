package org.agilewiki.jactor.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.Factory;
import org.agilewiki.jactor.util.durable.FactoryLocator;

public class RootTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            FactoryLocator factoryLocator = Durables.getFactoryLocator(mailboxFactory);
            Factory rootFactory = factoryLocator.getFactory(Root.FACTORY_NAME);
            Mailbox mailbox = mailboxFactory.createMailbox();
            Root root1 = (Root) rootFactory.newSerializable(mailbox, factoryLocator);
            int sl = root1.getSerializedLength();
            //assertEquals(56, sl);
            root1.clearReq().call();
            sl = root1.getSerializedLength();
            //assertEquals(56, sl);
            IncDes rootJid1a = (IncDes) root1.getValueReq().call();
            assertNull(rootJid1a);
            IncDes rpa = (IncDes) root1.resolvePathnameReq("0").call();
            assertNull(rpa);
            Root root11 = (Root) root1.copyReq(null).call();
            assertNotNull(root11);
            sl = root11.getSerializedLength();
            //assertEquals(56, sl);
            rpa = (IncDes) root11.resolvePathnameReq("0").call();
            assertNull(rpa);

            Factory stringAFactory = factoryLocator.getFactory(JAString.FACTORY_NAME);
            JAString paString1 = (JAString) stringAFactory.newSerializable(mailbox, factoryLocator);
            paString1.setValueReq("abc").call();
            byte[] sb = paString1.getSerializedBytesReq().call();
            root1.setValueReq(paString1.getType(), sb).call();
            JAString sj = (JAString) root1.getValueReq().call();
            assertEquals("abc", sj.getValueReq().call());

            Root root2 = (Root) rootFactory.newSerializable(mailbox, factoryLocator);
            sl = root2.getSerializedLength();
            //assertEquals(56, sl);
            root2.setValueReq(IncDes.FACTORY_NAME).call();
            boolean made = root2.makeValueReq(IncDes.FACTORY_NAME).call();
            assertEquals(false, made);
            IncDes incDes2a = (IncDes) root2.getValueReq().call();
            assertNotNull(incDes2a);
            sl = incDes2a.getSerializedLength();
            assertEquals(0, sl);
            sl = root2.getSerializedLength();
            //assertEquals(110, sl);
            rpa = (IncDes) root2.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            assertEquals(rpa, incDes2a);
            Root root22 = (Root) root2.copyReq(null).call();
            root2.clearReq().call();
            sl = root2.getSerializedLength();
            //assertEquals(56, sl);
            incDes2a = (IncDes) root2.getValueReq().call();
            assertNull(incDes2a);
            assertNotNull(root22);
            sl = root22.getSerializedLength();
            //assertEquals(110, sl);
            rpa = (IncDes) root22.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            sl = rpa.getSerializedLength();
            assertEquals(0, sl);

        } finally {
            mailboxFactory.close();
        }
    }
}
