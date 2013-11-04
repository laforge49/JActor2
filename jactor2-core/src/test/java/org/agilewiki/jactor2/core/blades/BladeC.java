package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class BladeC {
    private final Reactor reactor;

    public BladeC(final Facility _facility) throws Exception {
        this.reactor = new IsolationReactor(_facility);
    }

    public AsyncRequest<String> throwAReq() {
        return new AsyncRequest<String>(reactor) {
            @Override
            protected void processAsyncRequest() throws Exception {
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
