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
        final Blade1 blade1 = new Blade1(reactor);
        final Blade2 blade2 = new Blade2(reactor);
        final String result = blade2.hi2AReq(blade1).call();
        assertEquals("Hello world!", result);
        facility.close();
    }

    public void testc() throws Exception {
        System.out.println("testb");
        final Facility facility = new Facility();
        final Blade1 blade1 = new Blade1(new IsolationReactor(facility));
        final Blade2 blade2 = new Blade2(new IsolationReactor(facility));
        final String result = blade2.hi2AReq(blade1).call();
        assertEquals("Hello world!", result);
        facility.close();
    }
}
