package org.agilewiki.jactor2.core.isolation.simple;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class A extends IsolationBladeBase {
    public A() throws Exception {
    }

    public AO<Void> run() {
        return new AO<Void>("run") {

            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                System.out.println("running");
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
