package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final Facility facility = new Facility();
        final Reactor reactor = new NonBlockingReactor(facility);
        final BladeA bladeA = new BladeA(reactor);
        final BladeB bladeB = new BladeB(reactor);
        try {
            bladeB.throwRequest(bladeA).call();
        } catch (final SecurityException se) {
            facility.close();
            return;
        }
        facility.close();
        throw new Exception("Security exception was not caught");
    }

    public void testIII() throws Exception {
        System.out.println("testIII");
        final Facility facility = new Facility();
        final BladeA bladeA = new BladeA(new IsolationReactor(facility));
        final BladeB bladeB = new BladeB(new IsolationReactor(facility));
        try {
            bladeB.throwRequest(bladeA).call();
        } catch (final SecurityException se) {
            facility.close();
            return;
        }
        facility.close();
        throw new Exception("Security exception was not caught");
    }
}
