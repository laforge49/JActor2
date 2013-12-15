package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;

public class TupleTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = Durables.createPlant();
        try {
            final FactoryLocator factoryLocator = Durables
                    .getFactoryLocator(plant);
            Durables.registerTupleFactory(factoryLocator, "sst",
                    JAString.FACTORY_NAME, JAString.FACTORY_NAME);
            final Factory tjf = factoryLocator.getFactory("sst");
            final Reactor reactor = new NonBlockingReactor(plant);
            final Tuple t0 = (Tuple) tjf.newSerializable(reactor,
                    factoryLocator);
            final JAString e0 = (JAString) t0.iGetReq(0).call();
            assertNull(e0.getValueReq().call());
            final JAString e1 = (JAString) t0.iGetReq(1).call();
            assertNull(e1.getValueReq().call());
            e0.setValueReq("Apples").call();
            assertEquals("Apples", e0.getValueReq().call());
            e1.setValueReq("Oranges").call();
            assertEquals("Oranges", e1.getValueReq().call());
            final Tuple t1 = (Tuple) t0.copyReq(null).call();
            final JAString f0 = (JAString) t1.resolvePathnameReq("0").call();
            assertEquals("Apples", f0.getValueReq().call());
            final JAString f1 = (JAString) t1.resolvePathnameReq("1").call();
            assertEquals("Oranges", f1.getValueReq().call());

            final JAString jaString1 = (JAString) Durables.newSerializable(
                    plant, JAString.FACTORY_NAME);
            jaString1.setValueReq("Peaches").call();
            final byte[] sb = jaString1.getSerializedBytesReq().call();
            t1.iSetReq(1, sb).call();
            final JAString f1b = (JAString) t1.resolvePathnameReq("1").call();
            assertEquals("Peaches", f1b.getValueReq().call());
        } finally {
            plant.close();
        }
    }
}
