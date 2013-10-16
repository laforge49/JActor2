package org.agilewiki.jactor2.core.blade;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Test5 extends TestCase {
    public void testCascading() throws Exception {
        final Plant plant = new Plant();
        final BladeE bladeE = new BladeE(plant);
        final Reactor reactorA = new IsolationReactor(plant);
        final BladeA bladeA = new BladeA(reactorA);
        try {
            bladeE.throwRequest(bladeA).call();
        } catch (final SecurityException se) {
            // It's magic! We get the SecurityException, although our request
            // did not throw it, or return it as response. This shows that
            // child request exceptions are passed up to the parent request.
            plant.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }
}
