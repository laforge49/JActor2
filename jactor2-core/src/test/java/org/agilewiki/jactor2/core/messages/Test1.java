package org.agilewiki.jactor2.core.messages;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testa() throws Exception {
        final BasicPlant plant = new BasicPlant();
        final IsolationReactor reactor = new IsolationReactor();
        final Blade11 blade1 = new Blade11(reactor);
        final String result = blade1.hiSReq().call();
        assertEquals("Hello world!", result);
        plant.close();
    }
}
