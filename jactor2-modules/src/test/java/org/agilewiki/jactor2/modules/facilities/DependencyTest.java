package org.agilewiki.jactor2.modules.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.modules.Facility;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;

public class DependencyTest extends TestCase {
    public void test() throws Exception {
        final BasicPlant plant = new BasicPlant();
        try {
            plant.dependencyPropertyAReq("B", "A").call();
            plant.dependencyPropertyAReq("C", "B").call();
            final Facility a = plant.createFacilityAReq("A")
                    .call();
            final Facility b = plant.createFacilityAReq("B")
                    .call();
            final Facility c = plant.createFacilityAReq("C")
                    .call();
            PropertiesProcessor propertiesProcessor = plant.asFacility().getPropertiesProcessor();
            ImmutableProperties<Object> properties = propertiesProcessor.getImmutableState();
            System.out.println("before: "+properties);
            plant.purgeFacilitySReq("A").call();
            plant.asFacility().getPropertiesProcessor().getReactor().nullSReq().call(); //synchronize for the properties update
            properties = propertiesProcessor.getImmutableState();
            System.out.println("after: "+properties);
        } finally {
            plant.close();
        }
    }
}
