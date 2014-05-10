package org.agilewiki.jactor2.core.impl.stReactors;

import org.agilewiki.jactor2.core.impl.stRequests.RequestStImpl;
import org.agilewiki.jactor2.core.requests.RequestImpl;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * The inbox used by NonBlockingReactor, ThreadBoundReactor
 * and SwingBoundReactor. CommonInbox uses a single ArrayDeque for the local queue.
 *
 * @author monster
 */
public class CommonInbox extends Inbox {

    /**
     * Local queue for same-thread exchanges.
     */
    private final LinkedBlockingQueue<Object> localQueue;

    /**
     * Creates a CommonInbox.
     */
    public CommonInbox() {
        localQueue = new LinkedBlockingQueue<Object>();
    }

    @Override
    public boolean hasWork() {
        final boolean rv = !localQueue.isEmpty();
        return rv;
    }

    @Override
    public boolean isEmpty() {
        return hasWork();
    }

    @Override
    public boolean isIdle() {
        return !hasWork();
    }

    @Override
    protected void offerLocal(final RequestStImpl msg) {
        localQueue.offer(msg);
    }

    @Override
    public RequestStImpl poll() {
        return (RequestStImpl) localQueue.poll();
    }
}
