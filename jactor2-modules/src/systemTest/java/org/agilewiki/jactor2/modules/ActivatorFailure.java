package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;

public class ActivatorFailure {
    static public void main(final String[] _args) throws Exception {
        final BasicPlant plant = new BasicPlant();
        try {
            plant.activatorPropertyAReq("a", "NoSuchActivator").call();
            try {
                plant.createFacilityAReq("a").call();
            } catch (ServiceClosedException e) {
                plant.asFacility().getPropertiesProcessor().getReactor().nullSReq().call(); //synchronize for the properties update
                System.out.println(plant.asFacility().getPropertiesProcessor().getImmutableState());
            }
        } finally {
            plant.close();
        }
    }
}
