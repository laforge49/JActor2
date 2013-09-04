package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final ModuleContext moduleContext = new ModuleContext();
        final MessageProcessor messageProcessor = new NonBlockingMessageProcessor(moduleContext);
        final ActorA actorA = new ActorA(messageProcessor);
        final ActorB actorB = new ActorB(messageProcessor);
        try {
            actorB.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            moduleContext.close();
            return;
        }
        moduleContext.close();
        throw new Exception("Security exception was not caught");
    }

    public void testIII() throws Exception {
        System.out.println("testIII");
        final ModuleContext moduleContext = new ModuleContext();
        final ActorA actorA = new ActorA(new IsolationMessageProcessor(moduleContext));
        final ActorB actorB = new ActorB(new IsolationMessageProcessor(moduleContext));
        try {
            actorB.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            moduleContext.close();
            return;
        }
        moduleContext.close();
        throw new Exception("Security exception was not caught");
    }
}
