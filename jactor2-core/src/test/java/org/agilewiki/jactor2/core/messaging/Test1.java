package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testa() throws Exception {
        final Facility facility = new Facility();
        final Reactor reactor = new IsolationReactor(facility);
        final Actor1 actor1 = new Actor1(reactor);
        final String result = actor1.hiSReq().call();
        assertEquals("Hello world!", result);
        facility.close();
    }
}
