package org.agilewiki.pactor.util;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Blocks request processing, not threads.
 */
public class Semaphore {
    private final Mailbox mailbox;
    private int permits;
    private final Queue<ResponseProcessor<Void>> queue = new ArrayDeque<ResponseProcessor<Void>>();

    public Semaphore(final Mailbox mbox, final int permitCount) {
        this.mailbox = mbox;
        this.permits = permitCount;
    }

    public RequestBase<Void> acquire() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                if (permits > 0) {
                    permits -= 1;
                    responseProcessor.processResponse(null);
                } else {
                    queue.offer(responseProcessor);
                }
            }
        };
    }

    public RequestBase<Void> release() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                final ResponseProcessor<Void> rp = queue.poll();
                if (rp == null) {
                    permits += 1;
                } else {
                    rp.processResponse(null);
                }
                responseProcessor.processResponse(null);
            }
        };
    }
}
