package org.agilewiki.jactor2.core.impl.plant;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.impl.TestPlantConfiguration;

public class PlantTest extends TestCase {
    public void testa() throws Exception {
        new Plant(new TestPlantConfiguration());
        try {
        } finally {
            Plant.close();
        }
    }
}
