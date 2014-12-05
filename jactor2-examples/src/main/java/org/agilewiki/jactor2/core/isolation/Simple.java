package org.agilewiki.jactor2.core.isolation;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class Simple extends IsolationBladeBase {

    public static void main(final String[] args) throws Exception {
        new Plant();
        new TooSlow();
        System.out.println("initialized");
    }

    public Simple() throws Exception {
        new AIO("run") {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                AsyncResponseProcessor<Void> runResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        Plant.close();
                        System.out.println("finished");
                    }
                };
                _asyncRequestImpl.send(new Worker(0).run(), runResponseProcessor);
            }
        }.signal();
    }
}
