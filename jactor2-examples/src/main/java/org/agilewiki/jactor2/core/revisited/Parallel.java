package org.agilewiki.jactor2.core.revisited;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class Parallel extends IsolationBladeBase {

    public static void main(final String[] args) throws Exception {
        new Plant();
        new Parallel(5);
        System.out.println("initialized");
    }

    private Parallel(final int _p) throws Exception {
        new ASig("run") {
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                        final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                AsyncResponseProcessor<Void> runResponseProcessor =
                        new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        if (_asyncRequestImpl.hasNoPendingResponses()) {
                            Plant.close();
                            System.out.println("finished");
                        }
                    }
                };
                for (int i = 0; i < _p; i++)
                    _asyncRequestImpl.send(new Worker(i).run(100000000L, -1),
                            runResponseProcessor);
            }
        }.signal();
    }
}
