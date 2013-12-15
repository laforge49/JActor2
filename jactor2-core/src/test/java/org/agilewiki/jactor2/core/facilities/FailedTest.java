package org.agilewiki.jactor2.core.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.core.plant.Plant;

public class FailedTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = new Plant();
        try {
            plant.activatorPropertyAReq("A", "org.agilewiki.jactor2.core.facilities.SampleActivator").call();
            plant.failFacility("A", "inhibit");
            plant.autoStartAReq("A", true).call();
            PropertiesProcessor propertiesProcessor = plant.asFacility().getPropertiesProcessor();
            propertiesProcessor.getReactor().nullSReq().call(); //synchronize for the properties update
            System.out.println("before"+propertiesProcessor.getImmutableState());
            plant.clearFailedAReq("A").call();
            propertiesProcessor.getReactor().nullSReq().call(); //synchronize for the properties update
            System.out.println("after"+propertiesProcessor.getImmutableState());
            Thread.sleep(100); //give the activator a chance to run
        } finally {
            plant.close();
        }
    }
}
