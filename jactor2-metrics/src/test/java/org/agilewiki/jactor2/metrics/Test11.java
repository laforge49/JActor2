package org.agilewiki.jactor2.metrics;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class Test11 extends TestCase {
    public void test() throws Exception {
        MetricsTimerImpl.setupConsoleReporter(1000);
        new Plant(new MetricsPlantConfiguration());
        try {
            final IsolationReactor reactor = new IsolationReactor();
            final Blade11 blade1 = new Blade11(reactor);
            blade1.hiSReq().call();
            blade1.hiSReq().call();
            blade1.hiSReq().call();
            blade1.hoAReq().call();
            blade1.hoAReq().call();
            blade1.humASig().signal();
        } finally {
            Thread.sleep(1000);
            Plant.close();
        }
    }
}
