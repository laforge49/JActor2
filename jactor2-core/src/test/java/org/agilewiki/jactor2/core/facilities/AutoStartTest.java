package org.agilewiki.jactor2.core.facilities;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;

public class AutoStartTest extends TestCase {
    public void test() throws Exception {
        final Plant plant = new Plant();
        try {
            plant.activatorPropertyAReq("A", "org.agilewiki.jactor2.core.facilities.SampleActivator").call();
            plant.autoStartAReq("A", true).call();
            PropertiesProcessor propertiesProcessor = plant.getPropertiesProcessor();
            propertiesProcessor.getReactor().nullSReq().call(); //synchronize for the properties update
            ImmutableProperties<Object> properties = propertiesProcessor.getImmutableState();
            System.out.println(properties);
        } finally {
            plant.close();
        }
    }
}
