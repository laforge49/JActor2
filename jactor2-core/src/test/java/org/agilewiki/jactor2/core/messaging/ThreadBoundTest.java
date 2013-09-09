package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.ThreadBoundMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class ThreadBoundTest extends TestCase {
    ThreadBoundMessageProcessor boundMailbox;
    ModuleContext moduleContext;

    public void testa() throws Exception {
        moduleContext = new ModuleContext();
        boundMailbox = new ThreadBoundMessageProcessor(moduleContext, new Runnable() {
            @Override
            public void run() {
                boundMailbox.run();
                try {
                    moduleContext.close();
                } catch (final Throwable x) {
                }
            }
        });
        final MessageProcessor messageProcessor = new IsolationMessageProcessor(moduleContext);
        final Actor1 actor1 = new Actor1(messageProcessor);
        actor1.hiSReq().send(boundMailbox, new AsyncResponseProcessor<String>() {
            @Override
            public void processAsyncResponse(final String response) throws Exception {
                System.out.println(response);
                assertEquals("Hello world!", response);
            }
        });
    }
}
