package org.agilewiki.jactor2.core.messages;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final Plant plant = new Plant();
        final NonBlockingReactor reactor = new NonBlockingReactor(plant);
        new Blade4(reactor).hi4SReq().call();
        plant.close();
    }
}
