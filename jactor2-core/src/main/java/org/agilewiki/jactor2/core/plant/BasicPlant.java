package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.blades.NonBlockingBlade;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.impl.PlantImpl;

public interface BasicPlant extends NonBlockingBlade {
    Plant asPlant();
    Facility asFacility();
    PlantImpl asPlantImpl();
    void close() throws Exception;
    void exit();
}
