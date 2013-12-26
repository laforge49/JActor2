package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;

public class RootTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = Durables.createPlant();
        try {
            final FactoryLocator factoryLocator = Durables
                    .getFactoryLocator(plant);
            final Factory rootFactory = factoryLocator
                    .getFactory(Root.FACTORY_NAME);
            final Reactor reactor = new NonBlockingReactor();
            final Root root1 = (Root) rootFactory.newSerializable(reactor,
                    factoryLocator);
            int sl = root1.getSerializedLength();
            //assertEquals(56, sl);
            root1.clearReq().call();
            sl = root1.getSerializedLength();
            //assertEquals(56, sl);
            final IncDes rootJid1a = (IncDes) root1.getValueReq().call();
            assertNull(rootJid1a);
            IncDes rpa = (IncDes) root1.resolvePathnameReq("0").call();
            assertNull(rpa);
            final Root root11 = (Root) root1.copyReq(null).call();
            assertNotNull(root11);
            sl = root11.getSerializedLength();
            //assertEquals(56, sl);
            rpa = (IncDes) root11.resolvePathnameReq("0").call();
            assertNull(rpa);

            final Factory stringAFactory = factoryLocator
                    .getFactory(JAString.FACTORY_NAME);
            final JAString jaString1 = (JAString) stringAFactory
                    .newSerializable(reactor, factoryLocator);
            jaString1.setValueReq("abc").call();
            final byte[] sb = jaString1.getSerializedBytesReq().call();
            root1.setValueReq(jaString1.getFactoryName(), sb).call();
            final JAString sj = (JAString) root1.getValueReq().call();
            assertEquals("abc", sj.getValueReq().call());

            final Root root2 = (Root) rootFactory.newSerializable(reactor,
                    factoryLocator);
            sl = root2.getSerializedLength();
            //assertEquals(56, sl);
            root2.setValueReq(IncDes.FACTORY_NAME).call();
            final boolean made = root2.makeValueReq(IncDes.FACTORY_NAME).call();
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
            final Root root22 = (Root) root2.copyReq(null).call();
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
            plant.close();
        }
    }
}
