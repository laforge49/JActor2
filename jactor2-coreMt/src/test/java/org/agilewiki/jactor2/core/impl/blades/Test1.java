package org.agilewiki.jactor2.core.impl.blades;

import java.io.IOException;

import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.util.GwtIncompatible;

/**
 * Test code.
 */
@GwtIncompatible
public class Test1 extends CallTestBase {
    public void testI() throws Exception {
        new Plant();
        final Reactor reactor = new IsolationReactor();
        final BladeA bladeA = new BladeA(reactor);
        try {
            call(bladeA.throwRequest);
        } catch (final IOException se) {
            Plant.close();
            return;
        }
        throw new Exception("IOException was not caught");
    }

}
