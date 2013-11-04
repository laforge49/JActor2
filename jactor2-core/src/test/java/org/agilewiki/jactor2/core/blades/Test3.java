package org.agilewiki.jactor2.core.blades;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.facilities.Plant;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testI() throws Exception {
        final Plant plant = new Plant();
        final BladeC bladeC = new BladeC(plant);
        final String result = bladeC.throwAReq().call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        plant.close();
    }
}
