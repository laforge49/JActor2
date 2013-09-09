package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        final Actor3 actor3 = new Actor3(moduleContext);
        actor3.hi3SReq().call();
        moduleContext.close();
    }
}
