package org.agilewiki.jactor2.core.messages;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final Plant plant = new Plant();
        final Blade3 blade3 = new Blade3(plant);
        blade3.hi3SReq().call();
        plant.close();
    }
}
