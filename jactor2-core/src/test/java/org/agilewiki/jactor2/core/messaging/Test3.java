package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.context.JAContext;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final JAContext jaContext = new JAContext();
        final Actor3 actor3 = new Actor3(jaContext);
        actor3.hi3.call();
        jaContext.close();
    }
}
