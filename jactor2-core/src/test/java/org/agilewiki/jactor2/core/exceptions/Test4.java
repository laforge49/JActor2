package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testI() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        final ActorD actorD = new ActorD(moduleContext);
        final String result = actorD.throwAReq().call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        moduleContext.close();
    }
}
