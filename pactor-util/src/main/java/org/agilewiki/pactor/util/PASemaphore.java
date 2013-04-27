package org.agilewiki.pactor.util;

import org.agilewiki.pactor.api.*;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Blocks request processing, not threads.
 */
public class PASemaphore {
    /**
     * A mailbox is needed to handle requests.
     */
    private final Mailbox mailbox;

    /**
     * The number of available semaphores.
     */
    private int permits;

    /**
     * A queue of blocked requests.
     */
    private final Queue<ResponseProcessor<Void>> queue = new ArrayDeque<ResponseProcessor<Void>>();

    /**
     * The acquire request.
     */
    private final Request<Void> acquire;

    /**
     * The release request.
     */
    private final Request<Void> release;

    /**
     * Create a semaphore manager.
     *
     * @param mbox        The mailbox used to handle requests.
     * @param permitCount The number of semaphores initially available.
     */
    public PASemaphore(final Mailbox mbox, final int permitCount) {
        this.mailbox = mbox;
        this.permits = permitCount;

        acquire = new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
                    throws Exception {
                if (permits > 0) {
                    permits -= 1;
                    responseProcessor.processResponse(null);
                } else {
                    queue.offer(responseProcessor);
                }
            }
        };

        release = new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<Void> responseProcessor)
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

    /**
     * A request to acquire a semaphore.
     * The release pends until a semaphore is available.
     *
     * @return The request.
     */
    public Request<Void> acquireReq() {
        return acquire;
    }

    /**
     * A request to release a request.
     * If there is a pending request, a release will allow that request to complete.
     *
     * @return The request.
     */
    public Request<Void> releaseReq() {
        return release;
    }
}
