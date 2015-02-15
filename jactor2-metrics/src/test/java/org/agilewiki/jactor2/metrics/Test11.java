package org.agilewiki.jactor2.metrics;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class Test11 extends TestCase {
    public void test() throws Exception {
        MetricsTimerImpl.setupConsoleReporter(1000);
        new MetricsPlant();
        try {
            final IsolationReactor reactor = new IsolationReactor();
            final Blade11 blade1 = new Blade11(reactor);
            final String result = blade1.hiSOp().call();
            assertEquals("Hello world!", result);
        } finally {
            Thread.sleep(1000);
            MetricsPlant.close();
        }
    }
}
