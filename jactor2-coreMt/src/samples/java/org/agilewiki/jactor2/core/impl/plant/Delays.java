package org.agilewiki.jactor2.core.impl.plant;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.plant.DelayAReq;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public class Delays extends NonBlockingBladeBase {
    private final long count;

    Delays(final long _count) throws Exception {
        count = _count;
    }

    public AsyncRequest<Void> runAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncRequest<Void> dis = this;

            final AsyncResponseProcessor<Void> delayResponseProcessor =
                    new AsyncResponseProcessor<Void>() {
                        @Override
                        public void processAsyncResponse(final Void _response) {
                            if (dis.getPendingResponseCount() == 0)
                                dis.processAsyncResponse(null);
                        }
                    };

            public void processAsyncRequest() {
                long j = 0;
                while(j < count) {
                    j++;
                    DelayAReq delay = new DelayAReq(100);
                    send(delay, delayResponseProcessor);
                }
            }
        };
    }

    public static void main(final String[] _args) throws Exception {
        final long count = 10000L;
        new Plant(10);
        try {
            Delays delays = new Delays(count);
            AsyncRequest<Void> runAReq = delays.runAReq();
            final long before = System.currentTimeMillis();
            runAReq.call();
            final long after = System.currentTimeMillis();
            final long duration = after - before;
            System.out.println("Delay Test with " + count + " delays run in parallel");
            System.out.println("count: " + count);
            System.out.println("delay duration: 100 milliseconds each");
            System.out.println("total time: " + duration + " milliseconds");
        } finally {
            Plant.close();
        }
    }
}
