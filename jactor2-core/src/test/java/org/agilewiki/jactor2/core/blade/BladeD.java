package org.agilewiki.jactor2.core.blade;

import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.RequestBase;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class BladeD {
    private final Reactor reactor;

    public BladeD(final Facility _facility) {
        this.reactor = new IsolationReactor(_facility);
    }

    /**
     * Process the request immediately.
     *
     * @param _request        The request to be processed.
     * @param <RESPONSE_TYPE> The type of value returned.
     */
    protected <RESPONSE_TYPE> void send(final RequestBase<RESPONSE_TYPE> _request,
                                        final AsyncResponseProcessor<RESPONSE_TYPE> _responseProcessor)
            throws Exception {
        RequestBase.doSend(reactor, _request, _responseProcessor);
    }

    public AsyncRequest<String> throwAReq() {
        return new AsyncRequest<String>(reactor) {
            @Override
            protected void processAsyncRequest()
                    throws Exception {
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(final Exception exception)
                            throws Exception {
                        return exception.toString();
                    }
                });
                Dd dd = new Dd(targetReactor.getFacility());
                send(dd.doSomethinSReq(), new AsyncResponseProcessor<Void>() {
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
            protected Void processSyncRequest()
                    throws Exception {
                return null;
            }
        };
    }
}