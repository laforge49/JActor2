package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class ActorD {
    private final MessageProcessor messageProcessor;
    public final AsyncRequest<String> throwRequest;

    public ActorD(final ModuleContext _context) {
        this.messageProcessor = new IsolationMessageProcessor(_context);

        throwRequest = new AsyncRequest<String>(messageProcessor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                messageProcessor.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        processAsyncResponse(throwable.toString());
                    }
                });
                Dd dd = new Dd(messageProcessor.getModuleContext());
                dd.doSomethin.send(messageProcessor, new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void response)
                            throws Exception {
                        throw new SecurityException("thrown on request");
                    }
                });
            }
        };
    }
}

class Dd {
    private final MessageProcessor messageProcessor;
    final AsyncRequest<Void> doSomethin;

    public Dd(final ModuleContext _context) {
        messageProcessor = new IsolationMessageProcessor(_context);

        doSomethin = new AsyncRequest<Void>(messageProcessor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                processAsyncResponse(null);
            }
        };
    }
}