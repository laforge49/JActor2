package org.agilewiki.jactor2.core.blade;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final Facility facility = new Facility();
        final Reactor reactor = new IsolationReactor(facility);
        final BladeA bladeA = new BladeA(reactor);
        try {
            bladeA.throwRequest.call();
        } catch (final SecurityException se) {
            facility.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }

}
