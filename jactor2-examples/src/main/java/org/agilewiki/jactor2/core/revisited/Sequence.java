package org.agilewiki.jactor2.core.revisited;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

public class Sequence extends IsolationBladeBase {

    private Worker worker;
    private AsyncResponseProcessor<Void> runResponseProcessor;

    public static void main(final String[] args) throws Exception {
        new Plant();
        new Sequence(5);
        System.out.println("initialized");
    }

    private Sequence(final int maxCount) throws Exception {
        new ASig("run") {

            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                        final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                worker = new Worker(0);
                runResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(Void _response) throws Exception {
                        if (worker.getCount() < maxCount) {
                            _asyncRequestImpl.send(worker.run(100000000L, -1),
                                    runResponseProcessor);
                        } else {
                            Plant.close();
                            System.out.println("finished");
                        }
                    }
                };
                _asyncRequestImpl.send(worker.run(100000000L, -1), runResponseProcessor);
            }
        }.signal();
    }
}
