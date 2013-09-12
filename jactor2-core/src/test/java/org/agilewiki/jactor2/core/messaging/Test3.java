package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final Facility facility = new Facility();
        final Blade3 blade3 = new Blade3(facility);
        blade3.hi3SReq().call();
        facility.close();
    }
}
