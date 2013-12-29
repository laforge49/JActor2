package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Plant implements BasicPlant {

    public static Plant getSingleton() {
        return PlantImpl.getSingleton();
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

    public BasicPlant asBasicPlant() {
        return this;
    }

    @Override
    public Plant asPlant() {
        return this;
    }

    @Override
    public PlantImpl asPlantImpl() {
        return plantImpl;
    }

    @Override
    public NonBlockingReactor getReactor() {
        return plantImpl.getReactor();
    }

    @Override
    public void close() throws Exception {
        plantImpl.close();
    }

    @Override
    public void exit() {
        plantImpl.exit();
    }
}
