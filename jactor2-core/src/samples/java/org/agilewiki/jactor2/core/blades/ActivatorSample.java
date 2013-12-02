package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.facilities.Plant;

public class ActivatorSample {
    public static void main(String[] args) throws Exception {
        final Plant plant = new Plant();
        try {
            plant.activatorPropertyAReq("a", "org.agilewiki.jactor2.core.blades.SampleActivator").call();
            plant.createFacilityAReq("a").call();
        } finally {
            plant.close();
        }
    }
}
