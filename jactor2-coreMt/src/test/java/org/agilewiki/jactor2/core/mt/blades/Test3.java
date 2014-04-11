package org.agilewiki.jactor2.core.mt.blades;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.Plant;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testI() throws Exception {
        new Plant();
        final BladeC bladeC = new BladeC();
        final String result = bladeC.throwAReq().call();
        assertEquals("java.io.IOException: thrown on request", result);
        Plant.close();
    }
}
