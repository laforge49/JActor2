package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final Facility facility = new Facility();
        final Reactor reactor = new NonBlockingReactor(facility);
        new Actor4(reactor).hi4SReq().call();
        facility.close();
    }

    public void testd() throws Exception {
        final Facility facility = new Facility();
        new Actor4(new IsolationReactor(facility)).hi4SReq().call();
        facility.close();
    }
}
