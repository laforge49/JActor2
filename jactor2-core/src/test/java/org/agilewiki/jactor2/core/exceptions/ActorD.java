package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.messaging.SyncRequest;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

public class ActorD {
    private final Reactor reactor;

    public ActorD(final Facility _facility) {
        this.reactor = new IsolationReactor(_facility);
    }

    public AsyncRequest<String> throwAReq() {
        return new AsyncRequest<String>(reactor) {
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
    private final Reactor reactor;

    public Dd(final Facility _facility) {
        reactor = new IsolationReactor(_facility);
    }

    public SyncRequest<Void> doSomethinSReq() {
        return new SyncRequest<Void>(reactor) {
            @Override
            public Void processSyncRequest()
                    throws Exception {
                return null;
            }
        };
    }
}