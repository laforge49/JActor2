package org.agilewiki.jactor2.core.facilities;

import junit.framework.TestCase;
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
            ImmutableProperties<Object> properties = plant.getPropertiesProcessor().getImmutableState();
            System.out.println("before: "+properties);
            a.close();
            plant.getPropertiesProcessor().getReactor().nullSReq().call();
            properties = plant.getPropertiesProcessor().getImmutableState();
            System.out.println("after: "+properties);
        } finally {
            plant.close();
        }
    }
}
