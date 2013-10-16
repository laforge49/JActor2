package org.agilewiki.jactor2.core.blade;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.facilities.Plant;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testI() throws Exception {
        final Plant plant = new Plant();
        final BladeD bladeD = new BladeD(plant);
        final String result = bladeD.throwAReq().call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        plant.close();
    }
}
