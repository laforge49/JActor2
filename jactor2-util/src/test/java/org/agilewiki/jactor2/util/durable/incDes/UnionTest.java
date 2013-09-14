package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;

public class UnionTest extends TestCase {
    public void test() throws Exception {
        Facility facility = Durables.createFacility();
        try {
            FactoryLocator factoryLocator = Durables.getFactoryLocator(facility);
            Durables.registerUnionFactory(factoryLocator, "siUnion", JAString.FACTORY_NAME, "siUnion");
            Union siu1 = (Union) Durables.newSerializable(facility, "siUnion");
            assertNull(siu1.getValue());
            Reactor reactor = new NonBlockingReactor(facility);
            Union siu2 = (Union) siu1.copy(reactor);
            assertNull(siu2.getValue());
            siu2.setValue(JAString.FACTORY_NAME);
            JAString sj2 = (JAString) siu2.getValue();
            assertNotNull(sj2);
            Union siu3 = (Union) siu2.copy(reactor);
            JAString sj3 = (JAString) siu3.getValue();
            assertNotNull(sj3);
        } finally {
            facility.close();
        }
    }
}
