package org.agilewiki.jactor2.core.impl.plant;

import org.agilewiki.jactor2.core.impl.JActorStTestPlantConfiguration;
import org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration;

public class GwtTestLogTest extends BaseGWTTestCase {
    public void testa() throws Exception {
        final PlantConfiguration config = new JActorStTestPlantConfiguration();
        config.warn("exception test", new SecurityException("Ignore this"));
    }
}
