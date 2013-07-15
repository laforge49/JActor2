package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.api.*;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Blocks boundRequest processing, not threads.
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
     * The acquire boundRequest.
     */
    private final BoundRequest<Void> acquire;

    /**
     * The release boundRequest.
     */
    private final BoundRequest<Void> release;

    /**
     * Create a semaphore manager.
     *
     * @param mbox        The mailbox used to handle requests.
     * @param permitCount The number of semaphores initially available.
     */
    public PASemaphore(final Mailbox mbox, final int permitCount) {
        this.mailbox = mbox;
        this.permits = permitCount;

        acquire = new BoundRequestBase<Void>(mailbox) {
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

        release = new BoundRequestBase<Void>(mailbox) {
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
     * A boundRequest to acquire a semaphore.
     * The release pends until a semaphore is available.
     *
     * @return The boundRequest.
     */
    public BoundRequest<Void> acquireReq() {
        return acquire;
    }

    /**
     * A boundRequest to release a boundRequest.
     * If there is a pending boundRequest, a release will allow that boundRequest to complete.
     *
     * @return The boundRequest.
     */
    public BoundRequest<Void> releaseReq() {
        return release;
    }
}
