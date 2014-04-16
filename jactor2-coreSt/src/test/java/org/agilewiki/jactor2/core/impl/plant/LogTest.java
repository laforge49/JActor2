package org.agilewiki.jactor2.core.impl.plant;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.impl.TestPlantConfiguration;

public class LogTest extends TestCase {
    public void testa() throws Exception {
        TestPlantConfiguration config = new TestPlantConfiguration();
        config.warn("exception test", new SecurityException("Ignore this"));
    }
}
