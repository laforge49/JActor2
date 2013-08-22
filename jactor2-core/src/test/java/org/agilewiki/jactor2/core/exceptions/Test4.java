package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testI() throws Exception {
        final JAContext jaContext = new JAContext();
        final ActorD actorD = new ActorD(jaContext);
        final String result = actorD.throwRequest.call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        jaContext.close();
    }
}
