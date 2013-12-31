package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.plant.BasicPlant;

public class ActivatorSample {
    public static void main(String[] args) throws Exception {
        final BasicPlant plant = new BasicPlant();
        try {
            plant.activatorPropertyAReq("a", "org.agilewiki.jactor2.core.blades.SampleActivator").call();
            plant.createFacilityAReq("a").call();
        } finally {
            plant.close();
        }
    }
}
