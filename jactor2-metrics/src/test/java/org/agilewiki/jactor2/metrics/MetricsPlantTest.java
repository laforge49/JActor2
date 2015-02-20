package org.agilewiki.jactor2.metrics;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.Plant;

public class MetricsPlantTest extends TestCase {
    public void test() throws Exception {
        MetricsTimerImpl.setupConsoleReporter(1000);
        new Plant(new MetricsPlantConfiguration());
        try {
            System.out.println(":-)");
        } finally {
            Thread.sleep(1000);
            Plant.close();
        }
    }
}
