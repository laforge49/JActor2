package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.plant.Plant;

public class ActivatorSample {
    public static void main(String[] args) throws Exception {
        new MPlant();
        try {
            MPlant.activatorPropertyAReq("a", "org.agilewiki.jactor2.core.blades.SampleActivator").call();
            MPlant.createFacilityAReq("a").call();
        } finally {
            Plant.close();
        }
    }
}
