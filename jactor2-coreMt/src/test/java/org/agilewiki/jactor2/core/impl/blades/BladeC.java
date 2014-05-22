package org.agilewiki.jactor2.core.impl.blades;

import java.io.IOException;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

public class BladeC {
    private final Reactor reactor;

    public BladeC() throws Exception {
        this.reactor = new IsolationReactor();
    }

    public AsyncRequest<String> throwAReq() {
        return new AsyncRequest<String>(reactor) {
            @Override
            public void processAsyncRequest() throws Exception {
                setExceptionHandler(new ExceptionHandler<String>() {
                    @Override
                    public String processException(final Exception exception)
                            throws Exception {
                        return exception.toString();
                    }
                });
                throw new IOException("thrown on request");
            }
        };
    }
}
