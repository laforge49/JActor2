package org.agilewiki.jactor2.core.impl.blades;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.io.IOException;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        new Plant();
        final Reactor reactor = new IsolationReactor();
        final BladeA bladeA = new BladeA(reactor);
        try {
            bladeA.throwRequest.call();
        } catch (final IOException se) {
            Plant.close();
            return;
        }
        throw new Exception("IOException was not caught");
    }

}
