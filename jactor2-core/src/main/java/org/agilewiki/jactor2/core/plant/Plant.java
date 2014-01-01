package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Plant {

    public static Plant getSingleton() {
        return PlantImpl.getSingleton();
    }

    public static void close() throws Exception {
        Plant plant = getSingleton();
        if (plant != null)
            plant.asPlantImpl().close();
    }

    public static void exit() {
        Plant plant = getSingleton();
        if (plant != null)
            plant.asPlantImpl().exit();
    }

    private final PlantImpl plantImpl;

    public Plant() throws Exception {
        plantImpl = new PlantImpl();
        plantImpl.initialize(this);
    }

    public Plant(final int _threadCount) throws Exception {
        plantImpl = new PlantImpl();
        plantImpl.initialize(this, _threadCount);
    }

    public Plant(final PlantConfiguration _plantConfiguration) throws Exception {
        plantImpl = new PlantImpl();
        plantImpl.initialize(this, _plantConfiguration);
    }

    public PlantImpl asPlantImpl() {
        return plantImpl;
    }

    public NonBlockingReactor getReactor() {
        return plantImpl.getReactor();
    }
}
