package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testa() throws Exception {
        System.out.println("testa");
        final Facility facility = new Facility();
        final Reactor reactor = new NonBlockingReactor(facility);
        final Actor1 actor1 = new Actor1(reactor);
        final Actor2 actor2 = new Actor2(reactor);
        final String result = actor2.hi2AReq(actor1).call();
        assertEquals("Hello world!", result);
        facility.close();
    }

    public void testc() throws Exception {
        System.out.println("testb");
        final Facility facility = new Facility();
        final Actor1 actor1 = new Actor1(new IsolationReactor(facility));
        final Actor2 actor2 = new Actor2(new IsolationReactor(facility));
        final String result = actor2.hi2AReq(actor1).call();
        assertEquals("Hello world!", result);
        facility.close();
    }
}
