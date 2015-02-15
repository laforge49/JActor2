package org.agilewiki.jactor2.metrics;

import junit.framework.TestCase;

public class MetricsPlantTest extends TestCase {
    public void test() throws Exception {
        MetricsTimerImpl.setupConsoleReporter(1000);
        new MetricsPlant();
        try {
            System.out.println(":-)");
        } finally {
            Thread.sleep(1000);
            MetricsPlant.close();
        }
    }
}
