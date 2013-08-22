package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        final MessageProcessor messageProcessor = new AtomicMessageProcessor(moduleContext);
        final ActorA actorA = new ActorA(messageProcessor);
        try {
            actorA.throwRequest.call();
        } catch (final SecurityException se) {
            moduleContext.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }

}
