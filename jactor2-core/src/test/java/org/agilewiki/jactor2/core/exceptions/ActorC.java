package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class ActorC {
    private final MessageProcessor messageProcessor;
    public final Request<String> throwRequest;

    public ActorC(final ModuleContext _context) {
        this.messageProcessor = new IsolationMessageProcessor(_context);

        throwRequest = new Request<String>(messageProcessor) {
            @Override
            public void processRequest()
                    throws Exception {
                messageProcessor.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        processResponse(throwable.toString());
                    }
                });
                throw new SecurityException("thrown on request");
            }
        };
    }
}
