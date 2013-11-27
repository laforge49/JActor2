package org.agilewiki.jactor2.core.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;

public class DependencyTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = new Plant();
        try {
            final Facility a = plant.createFacilityAReq("A")
                    .call();
            final Facility b = plant.createFacilityAReq("B")
                    .call();
            final Facility c = plant.createFacilityAReq("C")
                    .call();
            b.dependencyAReq(a).call();
            c.dependencyAReq(b).call();
            PropertiesProcessor propertiesProcessor = plant.getPropertiesProcessor();
            ImmutableProperties<Object> properties = propertiesProcessor.getImmutableState();
            System.out.println("before: "+properties);
            plant.purgeFacilitySReq("A").call();
            plant.getPropertiesProcessor().getReactor().nullSReq().call(); //synchronize for the properties update
            properties = propertiesProcessor.getImmutableState();
            System.out.println("after: "+properties);
        } finally {
            plant.close();
        }
    }
}
