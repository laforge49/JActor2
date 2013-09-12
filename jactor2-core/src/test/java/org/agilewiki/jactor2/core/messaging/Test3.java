package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final Facility facility = new Facility();
        final Actor3 actor3 = new Actor3(facility);
        actor3.hi3SReq().call();
        facility.close();
    }
}
