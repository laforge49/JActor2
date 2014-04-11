package org.agilewiki.jactor2.core.mt.mtReactors;

import org.agilewiki.jactor2.core.requests.RequestImpl;

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
     * True when processing a request and the response has not yet been assigned.
     */
    private boolean processingRequest;

    /**
     * Local response-pending (requests) queue for same-thread exchanges.
     */
    private final ArrayDeque<RequestImpl> localResponsePendingQueue;

    /**
     * Local no-response-pending (events and responses) queue for same-thread exchanges.
     */
    private final ArrayDeque<RequestImpl> localNoResponsePendingQueue;

    /**
     * Creates an IsolationInbox.
     *
     * @param initialLocalQueueSize The initial doLocal queue size.
     */
    public IsolationInbox(final int initialLocalQueueSize) {
        concurrentQueue = new ConcurrentLinkedQueue<Object>();
        localResponsePendingQueue = new ArrayDeque<RequestImpl>(
                initialLocalQueueSize);
        localNoResponsePendingQueue = new ArrayDeque<RequestImpl>(
                initialLocalQueueSize);
    }

    /**
     * Add the messages in a message block to the appropriate doLocal queue.
     *
     * @param _msgs The message to be added.
     */
    private void offerLocal(final Queue<RequestImpl> _msgs) {
        while (!_msgs.isEmpty()) {
            final RequestImpl msg = _msgs.poll();
            offerLocal(msg);
        }
    }

    @Override
    protected void offerLocal(final RequestImpl msg) {
        if (!msg.isComplete() && !msg.isSignal()) {
            localResponsePendingQueue.offer(msg);
        } else {
            localNoResponsePendingQueue.offer(msg);
        }
    }

    @Override
    public boolean isEmpty() {
        return localResponsePendingQueue.isEmpty()
                && localNoResponsePendingQueue.isEmpty()
                && (concurrentQueue.peek() == null);
    }

    @Override
    public boolean isIdle() {
        return !processingRequest && isEmpty();
    }

    @Override
    public boolean hasWork() {
        while (localNoResponsePendingQueue.isEmpty()
                && (processingRequest || localResponsePendingQueue.isEmpty())) {
            final Object obj = concurrentQueue.poll();
            if (obj == null) {
                return false;
            }
            if (obj instanceof RequestImpl) {
                final RequestImpl msg = (RequestImpl) obj;
                offerLocal(msg);
            } else {
                final Queue<RequestImpl> msgs = (Queue<RequestImpl>) obj;
                offerLocal(msgs);
            }
        }
        return true;
    }

    @Override
    public RequestImpl poll() {
        if (!hasWork()) {
            return null;
        }
        final RequestImpl msg = localNoResponsePendingQueue.poll();
        if (msg != null) {
            return msg;
        } else {
            return localResponsePendingQueue.poll();
        }
    }

    @Override
    public void requestBegin(final RequestImpl _requestImpl) {
        if (_requestImpl.isSignal())
            return;
        if (processingRequest) {
            throw new IllegalStateException("already processing a request");
        }
//        System.out.println("processing request "+this);
        processingRequest = true;
    }

    @Override
    public void requestEnd(final RequestImpl _message) {
        if (_message.isSignal())
            return;
        if (!processingRequest) {
//            System.out.println("not processing request "+this);
            throw new IllegalStateException("not processing a request");
        }
        processingRequest = false;
    }
}
