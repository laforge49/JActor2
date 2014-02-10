package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;

import java.io.IOException;

public class BladeC {
    private final Reactor reactor;

    public BladeC(final Plant _plant) throws Exception {
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
