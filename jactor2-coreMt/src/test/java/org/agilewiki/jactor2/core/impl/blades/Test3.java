package org.agilewiki.jactor2.core.impl.blades;

import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;

/**
 * Test code.
 */
public class Test3 extends CallTestBase {
    public void testI() throws Exception {
        new Plant();
        final BladeC bladeC = new BladeC();
        final String result = call(bladeC.throwAOp());
        assertEquals("java.io.IOException: thrown on request", result);
        Plant.close();
    }
}
