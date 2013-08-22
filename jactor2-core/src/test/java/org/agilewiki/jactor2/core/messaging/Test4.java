package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        final MessageProcessor messageProcessor = new NonBlockingMessageProcessor(moduleContext);
        final Actor1 actor1 = new Actor1(messageProcessor);
        final Actor4 actor4 = new Actor4(messageProcessor);
        actor4.hi4(actor1).call();
        moduleContext.close();
    }

    public void testd() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        final Actor1 actor1 = new Actor1(new AtomicMessageProcessor(moduleContext));
        final Actor4 actor4 = new Actor4(new AtomicMessageProcessor(moduleContext));
        actor4.hi4(actor1).call();
        moduleContext.close();
    }
}
