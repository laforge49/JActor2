package org.agilewiki.jactor2.core.messaging;

import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

/**
 * Test code.
 */
public class Actor3 {
    private final MessageProcessor messageProcessor;
    public final AsyncRequest<Void> hi3;

    public Actor3(final ModuleContext _context) {
        this.messageProcessor = new IsolationMessageProcessor(_context);

        hi3 = new AsyncRequest<Void>(messageProcessor) {
            @Override
            public void processRequest()
                    throws Exception {
                System.out.println("Hello world!");
                processAsyncResponse(null);
            }
        };
    }
}
