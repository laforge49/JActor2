package org.agilewiki.jactor2.core.impl.plant;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.impl.TestPlantConfiguration;

public class PlantTest extends TestCase {
    public void testa() throws Exception {
        TestPlantConfiguration config = new TestPlantConfiguration();
        new Plant(new TestPlantConfiguration());
        try {
        } finally {
            config.warn("closing plant");
            Plant.close();
        }
    }
}
