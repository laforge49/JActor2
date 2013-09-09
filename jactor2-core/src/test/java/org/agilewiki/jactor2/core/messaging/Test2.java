package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testa() throws Exception {
        System.out.println("testa");
        final ModuleContext moduleContext = new ModuleContext();
        final MessageProcessor messageProcessor = new NonBlockingMessageProcessor(moduleContext);
        final Actor1 actor1 = new Actor1(messageProcessor);
        final Actor2 actor2 = new Actor2(messageProcessor);
        final String result = actor2.hi2AReq(actor1).call();
        assertEquals("Hello world!", result);
        moduleContext.close();
    }

    public void testc() throws Exception {
        System.out.println("testb");
        final ModuleContext moduleContext = new ModuleContext();
        final Actor1 actor1 = new Actor1(new IsolationMessageProcessor(moduleContext));
        final Actor2 actor2 = new Actor2(new IsolationMessageProcessor(moduleContext));
        final String result = actor2.hi2AReq(actor1).call();
        assertEquals("Hello world!", result);
        moduleContext.close();
    }
}
