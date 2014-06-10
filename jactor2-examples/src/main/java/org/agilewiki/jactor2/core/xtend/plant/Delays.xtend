package org.agilewiki.jactor2.core.xtend.plant;

import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.plant.DelayAReq;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.xtend.codegen.AReq

class Delays extends NonBlockingBladeBase {
    val long count;

    new(long _count) throws Exception {
        count = _count;
    }

	@AReq
    private def _run(AsyncRequest<Void> ar) {
        val delayResponseProcessor = new AsyncResponseProcessor<Void>() {
            override void processAsyncResponse(Void _response) {
                if (ar.getPendingResponseCount() == 0)
                    ar.processAsyncResponse(null);
            }
        };
        var j = 0L;
        while (j < count) {
            j++;
            val delay = new DelayAReq(100);
            ar.send(delay, delayResponseProcessor);
        }
    }

    def static void main(String[] _args) throws Exception {
        val count = 10000L;
        new Plant(10);
        try {
            val delays = new Delays(count);
            val runAReq = delays.runAReq();
            val before = System.currentTimeMillis();
            runAReq.call();
            val after = System.currentTimeMillis();
            val duration = after - before;
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
