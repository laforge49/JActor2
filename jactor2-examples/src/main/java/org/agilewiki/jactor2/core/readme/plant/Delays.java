package org.agilewiki.jactor2.core.readme.plant;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.plant.DelayAOp;

public class Delays extends NonBlockingBladeBase {
    private final long count;

    Delays(final long _count) throws Exception {
        count = _count;
    }

    public AOp<Void> runAOp() {
        return new AOp<Void>("run", getReactor()) {
            @Override
            protected void processAsyncOperation(
                    final AsyncRequestImpl _asyncRequestImpl,
                    final AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                final AsyncResponseProcessor<Void> delayResponseProcessor = new AsyncResponseProcessor<Void>() {
                    @Override
                    public void processAsyncResponse(final Void _response)
                            throws Exception {
                        if (_asyncRequestImpl.hasNoPendingResponses())
                            _asyncResponseProcessor.processAsyncResponse(null);
                    }
                };
                long j = 0;
                while (j < count) {
                    j++;
                    final DelayAOp delay = new DelayAOp(100);
                    _asyncRequestImpl.send(delay, delayResponseProcessor);
                }
            }
        };
    }

    public static void main(final String[] _args) throws Exception {
        final long count = 10000L;
        new Plant(10);
        try {
            final Delays delays = new Delays(count);
            final AOp<Void> runAReq = delays.runAOp();
            final long before = System.currentTimeMillis();
            runAReq.call();
            final long after = System.currentTimeMillis();
            final long duration = after - before;
            System.out.println("Delay Test with " + count
                    + " delays run in parallel");
            System.out.println("count: " + count);
            System.out.println("delay duration: 100 milliseconds each");
            System.out.println("total time: " + duration + " milliseconds");
        } finally {
            Plant.close();
        }
    }
}
