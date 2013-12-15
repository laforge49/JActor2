package org.agilewiki.jactor2.core.messages;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testa() throws Exception {
        final Plant plant = new Plant();
        final Reactor reactor = new IsolationReactor(plant);
        final Blade1 blade1 = new Blade1(reactor);
        final String result = blade1.hiSReq().call();
        assertEquals("Hello world!", result);
        plant.close();
    }
}
