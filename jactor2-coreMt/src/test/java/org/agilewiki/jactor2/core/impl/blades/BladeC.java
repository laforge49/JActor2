package org.agilewiki.jactor2.core.impl.blades;

import java.io.IOException;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class BladeC {
    private final Reactor reactor;

    public BladeC() throws Exception {
        this.reactor = new IsolationReactor();
    }

    public AOp<String> throwAOp() {
        return new AOp<String>("throw", reactor) {
            @Override
            public void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<String> _asyncResponseProcessor)
                    throws Exception {
                _asyncRequestImpl.setExceptionHandler(new ExceptionHandler<String>() {
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
