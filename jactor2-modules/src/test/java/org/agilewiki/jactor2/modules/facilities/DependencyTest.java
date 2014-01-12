package org.agilewiki.jactor2.modules.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.modules.Facility;
import org.agilewiki.jactor2.modules.MPlant;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesProcessor;

public class DependencyTest extends TestCase {
    public void test() throws Exception {
        new MPlant();
        try {
            MPlant.dependencyPropertyAReq("B", "A").call();
            MPlant.dependencyPropertyAReq("C", "B").call();
            final Facility a = MPlant.createFacilityAReq("A")
                    .call();
            final Facility b = MPlant.createFacilityAReq("B")
                    .call();
            final Facility c = MPlant.createFacilityAReq("C")
                    .call();
            PropertiesProcessor propertiesProcessor = MPlant.getInternalFacility().getPropertiesProcessor();
            ImmutableProperties<Object> properties = propertiesProcessor.getImmutableState();
            System.out.println("before: "+properties);
            MPlant.purgeFacilitySReq("A").call();
            MPlant.getInternalFacility().getPropertiesProcessor().getReactor().nullSReq().call(); //synchronize for the properties update
            properties = propertiesProcessor.getImmutableState();
            System.out.println("after: "+properties);
        } finally {
            Plant.close();
        }
    }
}
