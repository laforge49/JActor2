package org.agilewiki.jactor2.core.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;

public class AutoStartTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = new Plant();
        try {
            plant.activatorPropertyAReq("B", "org.agilewiki.jactor2.core.facilities.SampleActivator").call();
            plant.dependencyPropertyAReq("B", "A").call();
            plant.autoStartAReq("B", true).call();
            plant.autoStartAReq("A", true).call();
            PropertiesProcessor propertiesProcessor = plant.asFacility().getPropertiesProcessor();
            propertiesProcessor.getReactor().nullSReq().call(); //synchronize for the properties update
            ImmutableProperties<Object> properties = propertiesProcessor.getImmutableState();
            System.out.println(properties);
            Thread.sleep(100); //give the activator a chance to run
        } finally {
            plant.close();
        }
    }
}