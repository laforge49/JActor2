package org.agilewiki.jactor2.core.reactors.impl;

import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The inbox used by NonBlockingReactor, ThreadBoundReactor
 * and SwingBoundReactor. NonBlockingInbox uses a single ArrayDeque for the local queue.
 *
 * @author monster
 */
public class NonBlockingInbox extends Inbox {

    /**
     * Local queue for same-thread exchanges.
     */
    private final ArrayDeque<Object> localQueue;

    /**
     * Creates a NonBlockingInbox.
     *
     * @param initialLocalQueueSize The initial doLocal queue size.
     */
    public NonBlockingInbox(final int initialLocalQueueSize) {
        concurrentQueue = new ConcurrentLinkedQueue<Object>();
        localQueue = new ArrayDeque<Object>(initialLocalQueueSize);
    }

    @Override
    public boolean hasWork() {
        //ConcurrentLinkedQueue.isEmpty() is not accurate enough
        final boolean rv = !localQueue.isEmpty()
                || (concurrentQueue.peek() != null);
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
    protected void offerLocal(final RequestImpl msg) {
        localQueue.offer(msg);
    }

    @Override
    public RequestImpl poll() {
        Object obj = localQueue.peek();
        if (obj == null) {
            obj = concurrentQueue.poll();
            if (obj == null) {
                return null;
            } else {
                if (obj instanceof RequestImpl) {
                    return (RequestImpl) obj;
                } else {
                    @SuppressWarnings("unchecked")
                    final Queue<RequestImpl> msgs = (Queue<RequestImpl>) obj;
                    final RequestImpl result = msgs.poll();
                    if (!msgs.isEmpty()) {
                        // msgs is not empty so save it in localQueue
                        localQueue.offer(msgs);
                    }
                    return result;
                }
            }
        } else {
            if (obj instanceof RequestImpl) {
                return (RequestImpl) localQueue.poll();
            } else {
                @SuppressWarnings("unchecked")
                final Queue<RequestImpl> msgs = (Queue<RequestImpl>) obj;
                final RequestImpl result = msgs.poll();
                if (msgs.isEmpty()) {
                    // msgs is empty, so remove msgs from localQueue
                    localQueue.poll();
                }
                return result;
            }
        }
    }
}
