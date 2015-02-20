package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.impl.mtMessages.RequestMtImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The inbox used by IsolationReactor, the next request is not made available for processing
 * until a result is assigned to the previous request. This is implemented using
 * two ArrayDeques as the doLocal queues, one for requests and the other for events and
 * responses.
 */
public class IsolationInbox extends Inbox {

    /**
     * The request being processed to completion.
     */
    private RequestMtImpl<?> processingRequest;

    /**
     * Local response-pending (requests) queue for same-thread exchanges.
     */
    private final ArrayDeque<RequestMtImpl<?>> localResponsePendingQueue;

    /**
     * Local no-response-pending (events and responses) queue for same-thread exchanges.
     */
    private final ArrayDeque<RequestMtImpl<?>> localNoResponsePendingQueue;

    /**
     * Creates an IsolationInbox.
     *
     * @param initialLocalQueueSize The initial doLocal queue size.
     */
    public IsolationInbox(final int initialLocalQueueSize) {
        concurrentQueue = new ConcurrentLinkedQueue<Object>();
        localResponsePendingQueue = new ArrayDeque<RequestMtImpl<?>>(
                initialLocalQueueSize);
        localNoResponsePendingQueue = new ArrayDeque<RequestMtImpl<?>>(
                initialLocalQueueSize);
    }

    /**
     * Add the messages in a message block to the appropriate doLocal queue.
     *
     * @param _msgs The message to be added.
     */
    private void offerLocal(final Queue<RequestMtImpl<?>> _msgs) {
        while (!_msgs.isEmpty()) {
            final RequestMtImpl<?> msg = _msgs.poll();
            offerLocal(msg);
        }
    }

    @Override
    protected void offerLocal(final RequestMtImpl<?> msg) {
        if (msg.isComplete() || msg.isSignal()) {
            localNoResponsePendingQueue.offer(msg);
            return;
        }
        if (msg.getSourceReactor() != null && msg.getSourceReactor() == msg.getTargetReactor()) {
            localNoResponsePendingQueue.offer(msg);
            return;
        }
        RequestMtImpl<?> oldMsg = msg.getOldRequest();
        if (oldMsg != null && oldMsg.getIsolationReactor() != null) {
            localNoResponsePendingQueue.offer(msg);
            return;
        }
        localResponsePendingQueue.offer(msg);
    }

    @Override
    public boolean isEmpty() {
        return localResponsePendingQueue.isEmpty()
                && localNoResponsePendingQueue.isEmpty()
                && (concurrentQueue.peek() == null);
    }

    @Override
    public boolean isIdle() {
        return null == processingRequest && isEmpty();
    }

    @Override
    public boolean hasWork() {
        while (localNoResponsePendingQueue.isEmpty()
                && (processingRequest != null || localResponsePendingQueue.isEmpty())) {
            final Object obj = concurrentQueue.poll();
            if (obj == null) {
                return false;
            }
            if (obj instanceof RequestImpl) {
                final RequestMtImpl<?> msg = (RequestMtImpl<?>) obj;
                offerLocal(msg);
            } else {
                @SuppressWarnings("unchecked")
                final Queue<RequestMtImpl<?>> msgs = (Queue<RequestMtImpl<?>>) obj;
                offerLocal(msgs);
            }
        }
        return true;
    }

    @Override
    public RequestMtImpl<?> poll() {
        if (!hasWork()) {
            return null;
        }
        final RequestMtImpl<?> msg = localNoResponsePendingQueue.poll();
        if (msg != null) {
            return msg;
        } else {
            return localResponsePendingQueue.poll();
        }
    }

    @Override
    public void requestBegin(final RequestMtImpl<?> _requestImpl) {
        if (_requestImpl.isSignal()) {
            return;
        }
        if (processingRequest != null) {
            return;
        }
        processingRequest = _requestImpl;
    }

    @Override
    public void requestEnd(final RequestMtImpl<?> _requestImpl) {
        if (_requestImpl.isSignal()) {
            return;
        }
        if (processingRequest == null) {
            throw new IllegalStateException("not processing request:\n" + _requestImpl.toString());
        }
        if (processingRequest != _requestImpl)
            return;
        processingRequest = null;
    }
}
