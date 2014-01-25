package org.agilewiki.jactor2.modules;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.ServiceClosedException;

public class ActivatorFailure {
    static public void main(final String[] _args) throws Exception {
        new MPlant();
        try {
            MPlant.activatorPropertyAReq("a", "NoSuchActivator").call();
            try {
                Facility.createFacilityAReq("a").call();
            } catch (ServiceClosedException e) {
                Facility facility = MPlant.getInternalFacility();
                facility.nullSReq().call(); //synchronize for the properties update
                System.out.println(facility.getPropertiesProcessor().getImmutableState());
            }
        } finally {
            Plant.close();
        }
    }
}
