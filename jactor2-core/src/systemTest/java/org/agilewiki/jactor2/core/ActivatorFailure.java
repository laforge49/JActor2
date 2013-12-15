package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;

public class ActivatorFailure {
    static public void main(final String[] _args) throws Exception {
        final Plant plant = new Plant();
        try {
            plant.activatorPropertyAReq("a", "NoSuchActivator").call();
            try {
                plant.createFacilityAReq("a").call();
            } catch (ServiceClosedException e) {
                plant.getPropertiesProcessor().getReactor().nullSReq().call(); //synchronize for the properties update
                System.out.println(plant.getPropertiesProcessor().getImmutableState());
            }
        } finally {
            plant.close();
        }
    }
}
