package org.agilewiki.jactor2.core.impl.requests;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.impl.Plant;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        new Plant();
        final Blade3 blade3 = new Blade3();
        blade3.hi3SReq().call();
        Plant.close();
    }
}
