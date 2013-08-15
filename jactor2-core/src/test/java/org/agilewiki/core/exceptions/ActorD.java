package org.agilewiki.core.exceptions;

import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.AtomicMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class ActorD {
    private final MessageProcessor messageProcessor;
    public final Request<String> throwRequest;

    public ActorD(final MessageProcessor mbox) {
        this.messageProcessor = mbox;

        throwRequest = new Request<String>(messageProcessor) {
            @Override
            public void processRequest(
                    final Transport<String> responseProcessor)
                    throws Exception {
                messageProcessor.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        responseProcessor.processResponse(throwable.toString());
                    }
                });
                Dd dd = new Dd(new AtomicMessageProcessor(messageProcessor.getJAContext()));
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

    public Dd(final MessageProcessor mbox) {
        messageProcessor = mbox;

        doSomethin = new Request<Void>(messageProcessor) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                responseProcessor.processResponse(null);
            }
        };
    }
}