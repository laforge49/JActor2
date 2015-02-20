package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.impl.mtMessages.RequestMtImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private final ArrayDeque<Object> localQueue;

    /**
     * Creates a CommonInbox.
     *
     * @param initialLocalQueueSize The initial doLocal queue size.
     */
    public CommonInbox(final int initialLocalQueueSize) {
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
    protected void offerLocal(final RequestMtImpl<?> msg) {
        localQueue.offer(msg);
    }

    @Override
    public RequestMtImpl<?> poll() {
        Object obj = localQueue.peek();
        if (obj == null) {
            obj = concurrentQueue.poll();
            if (obj == null) {
                return null;
            } else {
                if (obj instanceof RequestImpl) {
                    return (RequestMtImpl<?>) obj;
                } else {
                    @SuppressWarnings("unchecked")
                    final Queue<RequestMtImpl<?>> msgs = (Queue<RequestMtImpl<?>>) obj;
                    final RequestMtImpl<?> result = msgs.poll();
                    if (!msgs.isEmpty()) {
                        // msgs is not empty so save it in localQueue
                        localQueue.offer(msgs);
                    }
                    return result;
                }
            }
        } else {
            if (obj instanceof RequestImpl) {
                return (RequestMtImpl<?>) localQueue.poll();
            } else {
                @SuppressWarnings("unchecked")
                final Queue<RequestMtImpl<?>> msgs = (Queue<RequestMtImpl<?>>) obj;
                final RequestMtImpl<?> result = msgs.poll();
                if (msgs.isEmpty()) {
                    // msgs is empty, so remove msgs from localQueue
                    localQueue.poll();
                }
                return result;
            }
        }
    }
}
