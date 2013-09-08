package org.agilewiki.jactor2.util;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.Event;
import org.agilewiki.jactor2.core.messaging.ResponseProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Blocks request processing, not threads.
 */
public class JASemaphore extends ActorBase {

    /**
     * The number of available semaphores.
     */
    private int permits;

    /**
     * A queue of blocked requests.
     */
    private final Queue<ResponseProcessor<Void>> queue = new ArrayDeque<ResponseProcessor<Void>>();

    /**
     * The release request.
     */
    private final Event<JASemaphore> release;

    /**
     * Create a semaphore manager.
     *
     * @param mbox        The processing used to handle requests.
     * @param permitCount The number of semaphores initially available.
     */
    public JASemaphore(final MessageProcessor mbox, final int permitCount) throws Exception {
        initialize(mbox);
        this.permits = permitCount;

        release = new Event<JASemaphore>() {
            @Override
            public void processEvent(JASemaphore _targetActor) throws Exception {
                final ResponseProcessor<Void> rp = queue.poll();
                if (rp == null) {
                    permits += 1;
                } else {
                    rp.processResponse(null);
                }
            }
        };
    }

    /**
     * A request to acquire a semaphore.
     * The release pends until a semaphore is available.
     *
     * @return The request.
     */
    public AsyncRequest<Void> acquireReq() {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest()
                    throws Exception {
                if (permits > 0) {
                    permits -= 1;
                    processResponse(null);
                } else {
                    queue.offer(this);
                }
            }
        };
    }

    /**
     * A request to release a request.
     * If there is a pending request, a release will allow that request to complete.
     */
    public void release() throws Exception {
        release.signal(this);
    }
}
