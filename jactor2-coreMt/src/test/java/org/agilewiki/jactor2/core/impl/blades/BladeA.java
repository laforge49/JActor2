package org.agilewiki.jactor2.core.impl.blades;

import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.io.IOException;

public class BladeA {
    private final Reactor reactor;
    public final AOp<Void> throwAOp;

    public BladeA(final Reactor mbox) {
        this.reactor = mbox;

        throwAOp = new AOp<Void>("throw", reactor) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                throw new IOException("thrown on request");
            }
        };
    }
}
