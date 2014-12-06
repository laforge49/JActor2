package org.agilewiki.jactor2.core.isolation;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class Worker extends IsolationBladeBase {
    public final String id;
    private int count;

    public Worker(final int _id) throws Exception {
        id = "Worker" + _id;
    }

    public int getCount() {
        return count;
    }

    public AO<Void> run() {
        return new AO<Void>("run" + id) {

            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                System.out.println(id + ": started  " + count++);
                for (long i = 0L; i < 1000000000L; i++);
                System.out.println(id + ": finished " + count++);
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }

    public AO<Void> hang() {
        return new AO<Void>("hang" + id) {

            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl,
                                                 final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                System.out.println(id + ": started  " + count++);
                for (long i = 0L; i < 1000000000000L; i++);
                System.out.println(id + ": finished " + count++);
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
