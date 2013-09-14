package org.agilewiki.jactor2.core.blade;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Test5 extends TestCase {
    public void testCascading() throws Exception {
        final Facility facility = new Facility();
        final BladeE bladeE = new BladeE(facility);
        final Reactor reactorA = new IsolationReactor(facility);
        final BladeA bladeA = new BladeA(reactorA);
        try {
            bladeE.throwRequest(bladeA).call();
        } catch (final SecurityException se) {
            // It's magic! We get the SecurityException, although our request
            // did not throw it, or return it as response. This shows that
            // child request exceptions are passed up to the parent request.
            facility.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }
}
