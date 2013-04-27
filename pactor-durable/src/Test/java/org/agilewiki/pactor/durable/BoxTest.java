package org.agilewiki.pactor.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;

public class BoxTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = DurableFactories.createMailboxFactory();
        try {
            FactoryLocator factoryLocator = Util.getFactoryLocator(mailboxFactory);
            Factory boxAFactory = factoryLocator.getFactory(Box.FACTORY_NAME);
            Mailbox mailbox = mailboxFactory.createMailbox();
            Box box1 = (Box) boxAFactory.newSerializable(mailbox);
            int sl = box1.getSerializedLength();
            assertEquals(4, sl);
            box1.clearReq().call();
            sl = box1.getSerializedLength();
            assertEquals(4, sl);
            IncDes incDes1a = (IncDes) box1.getIncDesReq().call();
            assertNull(incDes1a);
            IncDes rpa = (IncDes) box1.resolvePathnameReq("0").call();
            assertNull(rpa);
            Box box11 = (Box) box1.copyReq(null).call();
            assertNotNull(box11);
            sl = box11.getSerializedLength();
            assertEquals(4, sl);
            rpa = (IncDes) box11.resolvePathnameReq("0").call();
            assertNull(rpa);

            Factory stringAFactory = factoryLocator.getFactory(PAString.FACTORY_NAME);
            PAString string1 = (PAString) stringAFactory.newSerializable(mailbox, factoryLocator);
            string1.setStringReq("abc").call();
            byte[] sb = string1.getSerializedBytes();
            box1.setIncDesReq(string1.getType(), sb).call();
            PAString sj = (PAString) box1.getIncDesReq().call();
            assertEquals("abc", sj.getStringReq().call());

            Box box2 = (Box) Util.newSerializable(factoryLocator, Box.FACTORY_NAME, mailbox);
            sl = box2.getSerializedLength();
            assertEquals(4, sl);

            box2.setIncDesReq(IncDes.FACTORY_NAME).call();
            boolean made = box2.makeIncDesReq(IncDes.FACTORY_NAME).call();
            assertEquals(false, made);
            IncDes incDes2a = (IncDes) box2.getIncDesReq().call();
            assertNotNull(incDes2a);
            sl = incDes2a.getSerializedLength();
            assertEquals(0, sl);
            sl = box2.getSerializedLength();
            assertEquals(80, sl);
            rpa = (IncDes) box2.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            assertEquals(rpa, incDes2a);
            Box box22 = (Box) box2.copyReq(null).call();
            box2.clearReq().call();
            sl = box2.getSerializedLength();
            assertEquals(4, sl);
            incDes2a = (IncDes) box2.getIncDesReq().call();
            assertNull(incDes2a);
            assertNotNull(box22);
            sl = box22.getSerializedLength();
            assertEquals(80, sl);
            rpa = (IncDes) box22.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            sl = rpa.getSerializedLength();
            assertEquals(0, sl);

            Box box3 = (Box) Util.newSerializable(factoryLocator, Box.FACTORY_NAME, mailboxFactory);
            sl = box3.getSerializedLength();
            assertEquals(4, sl);
            made = box3.makeIncDesReq(Box.FACTORY_NAME).call();
            assertEquals(true, made);
            made = box3.makeIncDesReq(Box.FACTORY_NAME).call();
            assertEquals(false, made);
            Box box3a = (Box) box3.getIncDesReq().call();
            assertNotNull(box3a);
            sl = box3a.getSerializedLength();
            assertEquals(4, sl);
            sl = box3.getSerializedLength();
            assertEquals(78, sl);
            made = box3a.makeIncDesReq(IncDes.FACTORY_NAME).call();
            assertEquals(true, made);
            made = box3a.makeIncDesReq(IncDes.FACTORY_NAME).call();
            assertEquals(false, made);
            IncDes incDes3b = (IncDes) box3a.getIncDesReq().call();
            assertNotNull(incDes3b);
            sl = incDes3b.getSerializedLength();
            assertEquals(0, sl);
            sl = box3a.getSerializedLength();
            assertEquals(80, sl);
            sl = box3.getSerializedLength();
            assertEquals(154, sl);
            rpa = (IncDes) box3.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            assertEquals(rpa, box3a);
            rpa = (IncDes) box3.resolvePathnameReq("0/0").call();
            assertNotNull(rpa);
            assertEquals(rpa, incDes3b);
            Box box33 = (Box) box3.copyReq(null).call();
            box3a.clearReq().call();
            sl = box3a.getSerializedLength();
            assertEquals(4, sl);
            sl = box3.getSerializedLength();
            assertEquals(78, sl);
            incDes3b = (IncDes) box3a.getIncDesReq().call();
            assertNull(incDes3b);
            Box box3aa = (Box) box3.getIncDesReq().call();
            assertEquals(box3a, box3aa);
            assertNotNull(box33);
            sl = box33.getSerializedLength();
            assertEquals(154, sl);
            rpa = (IncDes) box33.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            sl = rpa.getSerializedLength();
            assertEquals(80, sl);
            rpa = (IncDes) box33.resolvePathnameReq("0/0").call();
            assertNotNull(rpa);
            sl = rpa.getSerializedLength();
            assertEquals(0, sl);

        } finally {
            mailboxFactory.close();
        }
    }
}
