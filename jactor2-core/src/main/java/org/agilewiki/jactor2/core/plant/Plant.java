package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

final public class Plant {

    public static void close() throws Exception {
        PlantImpl plantImpl = PlantImpl.getSingleton();
        if (plantImpl != null)
            plantImpl.close();
    }

    public static void exit() {
        PlantImpl plantImpl = PlantImpl.getSingleton();
        if (plantImpl != null)
            plantImpl.exit();
        else
            System.exit(10);
    }

    public static NonBlockingReactor getReactor() {
        return PlantImpl.getSingleton().getReactor();
    }

    public Plant() throws Exception {
        new PlantImpl();
    }

    public Plant(final int _threadCount) throws Exception {
        new PlantImpl(_threadCount);
    }

    public Plant(final PlantConfiguration _plantConfiguration) throws Exception {
        new PlantImpl(_plantConfiguration);
    }
}
