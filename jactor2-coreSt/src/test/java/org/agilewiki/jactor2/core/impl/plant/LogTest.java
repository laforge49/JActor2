package org.agilewiki.jactor2.core.impl.plant;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.TestPlantConfiguration;
import org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration;

public class LogTest extends TestCase {
    public void testa() throws Exception {
        PlantConfiguration config = new TestPlantConfiguration();
        config.warn("exception test", new SecurityException("Ignore this"));
    }
}
