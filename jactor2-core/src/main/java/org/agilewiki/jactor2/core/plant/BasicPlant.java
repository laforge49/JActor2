package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.facilities.Facility;

public interface BasicPlant {
    Plant asPlant();
    Facility asFacility();
    PlantImpl asPlantImpl();
    boolean startedClosing();
    void close() throws Exception;
    boolean isExitOnClose();
    void exit();
    boolean isForcedExit();
    void forceExit();
}
