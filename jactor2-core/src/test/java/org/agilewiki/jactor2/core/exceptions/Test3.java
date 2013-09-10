package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testI() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        final ActorC actorC = new ActorC(moduleContext);
        final String result = actorC.throwAReq().call();
        assertEquals("java.lang.SecurityException: thrown on request", result);
        moduleContext.close();
    }
}
