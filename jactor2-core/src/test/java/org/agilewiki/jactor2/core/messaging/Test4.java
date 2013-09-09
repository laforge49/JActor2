package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        final MessageProcessor messageProcessor = new NonBlockingMessageProcessor(moduleContext);
        new Actor4(messageProcessor).hi4SReq().call();
        moduleContext.close();
    }

    public void testd() throws Exception {
        final ModuleContext moduleContext = new ModuleContext();
        new Actor4(new IsolationMessageProcessor(moduleContext)).hi4SReq().call();
        moduleContext.close();
    }
}
