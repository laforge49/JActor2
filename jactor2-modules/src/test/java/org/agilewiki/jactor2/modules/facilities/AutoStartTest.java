package org.agilewiki.jactor2.modules.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;
import org.agilewiki.jactor2.modules.MPlant;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesProcessor;

public class AutoStartTest extends TestCase {
    public void test() throws Exception {
        new MPlant();
        try {
            MPlant.activatorPropertyAReq("B", "org.agilewiki.jactor2.modules.facilities.SampleActivator").call();
            MPlant.dependencyPropertyAReq("B", "A").call();
            MPlant.autoStartAReq("B", true).call();
            MPlant.autoStartAReq("A", true).call();
            PropertiesProcessor propertiesProcessor = MPlant.getInternalFacility().getPropertiesProcessor();
            propertiesProcessor.getReactor().nullSReq().call(); //synchronize for the properties update
            ImmutableProperties<Object> properties = propertiesProcessor.getImmutableState();
            System.out.println(properties);
            Thread.sleep(100); //give the activator a chance to run
        } finally {
            Plant.close();
        }
    }
}
