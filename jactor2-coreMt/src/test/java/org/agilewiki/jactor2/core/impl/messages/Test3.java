package org.agilewiki.jactor2.core.impl.messages;

import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;

/**
 * Test code.
 */
public class Test3 extends CallTestBase {
    public void testb() throws Exception {
        new Plant();
        final Blade3 blade3 = new Blade3();
        call(blade3.hi3SOp());
        Plant.close();
    }
}
