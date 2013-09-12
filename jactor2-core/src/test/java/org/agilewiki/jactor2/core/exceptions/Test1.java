package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

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
