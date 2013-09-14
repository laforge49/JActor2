package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;

public class TupleTest extends TestCase {
    public void test() throws Exception {
        Facility facility = Durables.createFacility();
        try {
            FactoryLocator factoryLocator = Durables.getFactoryLocator(facility);
            Durables.registerTupleFactory(factoryLocator,
                    "sst", JAString.FACTORY_NAME, JAString.FACTORY_NAME);
            Factory tjf = factoryLocator.getFactory("sst");
            Reactor reactor = new NonBlockingReactor(facility);
            Tuple t0 = (Tuple) tjf.newSerializable(reactor, factoryLocator);
            JAString e0 = (JAString) t0.iGetReq(0).call();
            assertNull(e0.getValueReq().call());
            JAString e1 = (JAString) t0.iGetReq(1).call();
            assertNull(e1.getValueReq().call());
            e0.setValueReq("Apples").call();
            assertEquals("Apples", e0.getValueReq().call());
            e1.setValueReq("Oranges").call();
            assertEquals("Oranges", e1.getValueReq().call());
            Tuple t1 = (Tuple) t0.copyReq(null).call();
            JAString f0 = (JAString) t1.resolvePathnameReq("0").call();
            assertEquals("Apples", f0.getValueReq().call());
            JAString f1 = (JAString) t1.resolvePathnameReq("1").call();
            assertEquals("Oranges", f1.getValueReq().call());

            JAString jaString1 = (JAString) Durables.newSerializable(facility, JAString.FACTORY_NAME);
            jaString1.setValueReq("Peaches").call();
            byte[] sb = jaString1.getSerializedBytesReq().call();
            t1.iSetReq(1, sb).call();
            JAString f1b = (JAString) t1.resolvePathnameReq("1").call();
            assertEquals("Peaches", f1b.getValueReq().call());
        } finally {
            facility.close();
        }
    }
}
