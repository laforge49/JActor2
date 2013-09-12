package org.agilewiki.jactor2.core.exceptions;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

public class BladeC {
    private final Reactor reactor;

    public BladeC(final Facility _facility) {
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
                throw new SecurityException("thrown on request");
            }
        };
    }
}
