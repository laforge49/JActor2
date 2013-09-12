package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testI() throws Exception {
        final Facility facility = new Facility();
        final BladeD bladeD = new BladeD(facility);
        final String result = bladeD.throwAReq().call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        facility.close();
    }
}
