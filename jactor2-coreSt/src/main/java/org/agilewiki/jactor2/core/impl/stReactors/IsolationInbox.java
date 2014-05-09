package org.agilewiki.jactor2.core.impl.stReactors;

import org.agilewiki.jactor2.core.impl.stRequests.RequestStImpl;
import org.agilewiki.jactor2.core.requests.RequestImpl;

import java.util.concurrent.LinkedBlockingQueue;

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
    private final LinkedBlockingQueue<RequestStImpl> localResponsePendingQueue;

    /**
     * Local no-response-pending (events and responses) queue for same-thread exchanges.
     */
    private final LinkedBlockingQueue<RequestStImpl> localNoResponsePendingQueue;

    /**
     * Creates an IsolationInbox.
     *
     */
    public IsolationInbox() {
        localResponsePendingQueue = new LinkedBlockingQueue<RequestStImpl>();
        localNoResponsePendingQueue = new LinkedBlockingQueue<RequestStImpl>();
    }

    @Override
    protected void offerLocal(final RequestStImpl msg) {
        if (!msg.isComplete() && !msg.isSignal()) {
            localResponsePendingQueue.offer(msg);
        } else {
            localNoResponsePendingQueue.offer(msg);
        }
    }

    @Override
    public boolean isEmpty() {
        return localResponsePendingQueue.isEmpty()
                && localNoResponsePendingQueue.isEmpty();
    }

    @Override
    public boolean isIdle() {
        return !processingRequest && isEmpty();
    }

    @Override
    public boolean hasWork() {
        if (localNoResponsePendingQueue.isEmpty()
                && (processingRequest || localResponsePendingQueue.isEmpty())) {
            return false;
        }
        return true;
    }

    @Override
    public RequestStImpl poll() {
        if (!hasWork()) {
            return null;
        }
        final RequestStImpl msg = localNoResponsePendingQueue.poll();
        if (msg != null) {
            return msg;
        } else {
            return localResponsePendingQueue.poll();
        }
    }

    @Override
    public void requestBegin(final RequestStImpl _requestImpl) {
        if (_requestImpl.isSignal())
            return;
        if (processingRequest) {
            throw new IllegalStateException("already processing a request");
        }
        processingRequest = true;
    }

    @Override
    public void requestEnd(final RequestStImpl _message) {
        if (_message.isSignal())
            return;
        if (!processingRequest) {
            throw new IllegalStateException("not processing a request");
        }
        processingRequest = false;
    }
}
