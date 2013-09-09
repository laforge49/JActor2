package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class Test5 extends TestCase {
    public void testCascading() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        final ActorE actorE = new ActorE(moduleContext);
        final MessageProcessor messageProcessorA = new IsolationMessageProcessor(moduleContext);
        final ActorA actorA = new ActorA(messageProcessorA);
        try {
            actorE.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            // It's magic! We get the SecurityException, although our request
            // did not throw it, or return it as response. This shows that
            // child request exceptions are passed up to the parent request.
            moduleContext.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }
}