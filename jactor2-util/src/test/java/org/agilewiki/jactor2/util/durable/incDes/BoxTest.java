package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;

public class BoxTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = Durables.createPlant();
        try {
            final FactoryLocator factoryLocator = Durables
                    .getFactoryLocator(plant);
            final Factory boxAFactory = factoryLocator
                    .getFactory(Box.FACTORY_NAME);
            final Reactor reactor = new NonBlockingReactor(plant);
            final Box box1 = (Box) boxAFactory.newSerializable(reactor);
            int sl = box1.getSerializedLength();
            assertEquals(4, sl);
            box1.clearReq().call();
            sl = box1.getSerializedLength();
            assertEquals(4, sl);
            final IncDes incDes1a = (IncDes) box1.getValueReq().call();
            assertNull(incDes1a);
            IncDes rpa = (IncDes) box1.resolvePathnameReq("0").call();
            assertNull(rpa);
            final Box box11 = (Box) box1.copyReq(null).call();
            assertNotNull(box11);
            sl = box11.getSerializedLength();
            assertEquals(4, sl);
            rpa = (IncDes) box11.resolvePathnameReq("0").call();
            assertNull(rpa);

            final Factory stringAFactory = factoryLocator
                    .getFactory(JAString.FACTORY_NAME);
            final JAString string1 = (JAString) stringAFactory.newSerializable(
                    reactor, factoryLocator);
            string1.setValueReq("abc").call();
            final byte[] sb = string1.getSerializedBytes();
            box1.setValueReq(string1.getFactoryName(), sb).call();
            final JAString sj = (JAString) box1.getValueReq().call();
            assertEquals("abc", sj.getValueReq().call());

            final Box box2 = (Box) Durables.newSerializable(factoryLocator,
                    Box.FACTORY_NAME, reactor);
            sl = box2.getSerializedLength();
            assertEquals(4, sl);

            box2.setValueReq(IncDes.FACTORY_NAME).call();
            boolean made = box2.makeValueReq(IncDes.FACTORY_NAME).call();
            assertEquals(false, made);
            IncDes incDes2a = (IncDes) box2.getValueReq().call();
            assertNotNull(incDes2a);
            sl = incDes2a.getSerializedLength();
            assertEquals(0, sl);
            sl = box2.getSerializedLength();
            assertEquals(92, sl);
            rpa = (IncDes) box2.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            assertEquals(rpa, incDes2a);
            final Box box22 = (Box) box2.copyReq(null).call();
            box2.clearReq().call();
            sl = box2.getSerializedLength();
            assertEquals(4, sl);
            incDes2a = (IncDes) box2.getValueReq().call();
            assertNull(incDes2a);
            assertNotNull(box22);
            sl = box22.getSerializedLength();
            assertEquals(92, sl);
            rpa = (IncDes) box22.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            sl = rpa.getSerializedLength();
            assertEquals(0, sl);

            final Box box3 = (Box) Durables.newSerializable(factoryLocator,
                    Box.FACTORY_NAME, plant);
            sl = box3.getSerializedLength();
            assertEquals(4, sl);
            made = box3.makeValueReq(Box.FACTORY_NAME).call();
            assertEquals(true, made);
            made = box3.makeValueReq(Box.FACTORY_NAME).call();
            assertEquals(false, made);
            final Box box3a = (Box) box3.getValueReq().call();
            assertNotNull(box3a);
            sl = box3a.getSerializedLength();
            assertEquals(4, sl);
            sl = box3.getSerializedLength();
            assertEquals(90, sl);
            made = box3a.makeValueReq(IncDes.FACTORY_NAME).call();
            assertEquals(true, made);
            made = box3a.makeValueReq(IncDes.FACTORY_NAME).call();
            assertEquals(false, made);
            IncDes incDes3b = (IncDes) box3a.getValueReq().call();
            assertNotNull(incDes3b);
            sl = incDes3b.getSerializedLength();
            assertEquals(0, sl);
            sl = box3a.getSerializedLength();
            assertEquals(92, sl);
            sl = box3.getSerializedLength();
            assertEquals(178, sl);
            rpa = (IncDes) box3.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            assertEquals(rpa, box3a);
            rpa = (IncDes) box3.resolvePathnameReq("0/0").call();
            assertNotNull(rpa);
            assertEquals(rpa, incDes3b);
            final Box box33 = (Box) box3.copyReq(null).call();
            box3a.clearReq().call();
            sl = box3a.getSerializedLength();
            assertEquals(4, sl);
            sl = box3.getSerializedLength();
            assertEquals(90, sl);
            incDes3b = (IncDes) box3a.getValueReq().call();
            assertNull(incDes3b);
            final Box box3aa = (Box) box3.getValueReq().call();
            assertEquals(box3a, box3aa);
            assertNotNull(box33);
            sl = box33.getSerializedLength();
            assertEquals(178, sl);
            rpa = (IncDes) box33.resolvePathnameReq("0").call();
            assertNotNull(rpa);
            sl = rpa.getSerializedLength();
            assertEquals(92, sl);
            rpa = (IncDes) box33.resolvePathnameReq("0/0").call();
            assertNotNull(rpa);
            sl = rpa.getSerializedLength();
            assertEquals(0, sl);

        } finally {
            plant.close();
        }
    }
}
