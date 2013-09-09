package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testa() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        final MessageProcessor messageProcessor = new IsolationMessageProcessor(moduleContext);
        final Actor1 actor1 = new Actor1(messageProcessor);
        final String result = actor1.hiSReq().call();
        assertEquals("Hello world!", result);
        moduleContext.close();
    }
}
