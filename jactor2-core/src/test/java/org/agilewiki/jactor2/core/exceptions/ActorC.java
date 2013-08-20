package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

public class ActorC {
    private final MessageProcessor messageProcessor;
    public final Request<String> throwRequest;

    public ActorC(final MessageProcessor mbox) {
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
                throw new SecurityException("thrown on request");
            }
        };
    }
}
