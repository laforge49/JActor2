package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class ActorD {
    private final MessageProcessor messageProcessor;
    public final Request<String> throwRequest;

    public ActorD(final ModuleContext _context) {
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
                Dd dd = new Dd(messageProcessor.getModuleContext());
                dd.doSomethin.send(messageProcessor, new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(final Void response)
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
    final Request<Void> doSomethin;

    public Dd(final ModuleContext _context) {
        messageProcessor = new IsolationMessageProcessor(_context);

        doSomethin = new Request<Void>(messageProcessor) {
            @Override
            public void processRequest()
                    throws Exception {
                processResponse(null);
            }
        };
    }
}