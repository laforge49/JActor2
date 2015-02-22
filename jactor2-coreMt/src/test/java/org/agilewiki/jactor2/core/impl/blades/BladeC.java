package org.agilewiki.jactor2.core.impl.blades;

import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.io.IOException;

public class BladeC {
    private final Reactor reactor;

    public BladeC() throws Exception {
        this.reactor = new IsolationReactor();
    }

    public AOp<String> throwAOp() {
        return new AOp<String>("throw", reactor) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
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
