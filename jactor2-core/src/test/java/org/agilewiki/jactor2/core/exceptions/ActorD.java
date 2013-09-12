package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.SyncRequest;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.Facility;

public class ActorD {
    private final MessageProcessor messageProcessor;

    public ActorD(final Facility _facility) {
        this.messageProcessor = new IsolationMessageProcessor(_facility);
    }

    public AsyncRequest<String> throwAReq() {
        return new AsyncRequest<String>(messageProcessor) {
            @Override
            public void processAsyncRequest()
                    throws Exception {
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(final Exception exception)
                            throws Exception {
                        return exception.toString();
                    }
                });
                Dd dd = new Dd(messageProcessor.getFacility());
                dd.doSomethinSReq().send(messageProcessor, new AsyncResponseProcessor<Void>() {
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

    public Dd(final Facility _facility) {
        messageProcessor = new IsolationMessageProcessor(_facility);
    }

    public SyncRequest<Void> doSomethinSReq() {
        return new SyncRequest<Void>(messageProcessor) {
            @Override
            public Void processSyncRequest()
                    throws Exception {
                return null;
            }
        };
    }
}