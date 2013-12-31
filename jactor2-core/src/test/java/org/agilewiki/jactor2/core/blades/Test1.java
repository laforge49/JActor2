package org.agilewiki.jactor2.core.blades;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final BasicPlant plant = new BasicPlant();
        final Reactor reactor = new IsolationReactor();
        final BladeA bladeA = new BladeA(reactor);
        try {
            bladeA.throwRequest.call();
        } catch (final SecurityException se) {
            plant.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }

}
