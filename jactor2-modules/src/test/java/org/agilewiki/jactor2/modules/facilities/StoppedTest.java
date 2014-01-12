package org.agilewiki.jactor2.modules.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.modules.MPlant;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesProcessor;

public class StoppedTest extends TestCase {
    public void test() throws Exception {
        new MPlant();
        try {
            MPlant.activatorPropertyAReq("A", "org.agilewiki.jactor2.modules.facilities.SampleActivator").call();
            MPlant.stopFacility("A");
            MPlant.autoStartAReq("A", true).call();
            PropertiesProcessor propertiesProcessor = MPlant.getInternalFacility().getPropertiesProcessor();
            propertiesProcessor.getReactor().nullSReq().call(); //synchronize for the properties update
            System.out.println("before"+propertiesProcessor.getImmutableState());
            MPlant.clearStoppedAReq("A").call();
            propertiesProcessor.getReactor().nullSReq().call(); //synchronize for the properties update
            System.out.println("after"+propertiesProcessor.getImmutableState());
            Thread.sleep(100); //give the activator a chance to run
        } finally {
            Plant.close();
        }
    }
}
