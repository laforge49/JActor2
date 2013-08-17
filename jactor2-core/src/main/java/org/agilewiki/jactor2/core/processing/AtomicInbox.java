package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.messaging.Message;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The inbox used by AtomicMessageProcessor, the next request is not made available for processing
 * until a result is assigned to the previous request. This is implemented using
 * two ArrayDeques as the local queues, one for requests and the other for events and
 * responses.
 */
public class AtomicInbox extends Inbox {

    /**
     * True when processing a request and the response has not yet been assigned.
     */
    private boolean processingRequest;

    /**
     * Local response-pending (requests) queue for same-thread exchanges.
     */
    private final ArrayDeque<Message> localResponsePendingQueue;

    /**
     * Local no-response-pending (events and responses) queue for same-thread exchanges.
     */
    private final ArrayDeque<Message> localNoResponsePendingQueue;

    /**
     * Creates an AtomicInbox.
     *
     * @param initialLocalQueueSize The initial local queue size.
     */
    public AtomicInbox(final int initialLocalQueueSize) {
        concurrentQueue = new ConcurrentLinkedQueue<Object>();
        if (initialLocalQueueSize > DEFAULT_INITIAL_LOCAL_QUEUE_SIZE) {
            localResponsePendingQueue = new ArrayDeque<Message>(initialLocalQueueSize);
            localNoResponsePendingQueue = new ArrayDeque<Message>(initialLocalQueueSize);
        } else {
            localResponsePendingQueue = new ArrayDeque<Message>(DEFAULT_INITIAL_LOCAL_QUEUE_SIZE);
            localNoResponsePendingQueue = new ArrayDeque<Message>(DEFAULT_INITIAL_LOCAL_QUEUE_SIZE);
        }
    }

    /**
     * Add the messages in a message block to the appropriate local queue.
     *
     * @param _msgs The message to be added.
     */
    private void offerLocal(final Queue<Message> _msgs) {
        while (!_msgs.isEmpty()) {
            Message msg = _msgs.poll();
            offerLocal(msg);
        }
    }

    @Override
    protected void offerLocal(final Message msg) {
        if (msg.isResponsePending())
            localResponsePendingQueue.offer(msg);
        else
            localNoResponsePendingQueue.offer(msg);
    }

    @Override
    public boolean isEmpty() {
        return localResponsePendingQueue.isEmpty() &&
                localNoResponsePendingQueue.isEmpty() &&
                concurrentQueue.peek() == null;
    }

    @Override
    public boolean isIdle() {
        return !processingRequest && isEmpty();
    }

    @Override
    public boolean hasWork() {
        while (localNoResponsePendingQueue.isEmpty() &&
                (processingRequest || localResponsePendingQueue.isEmpty())) {
            Object obj = concurrentQueue.poll();
            if (obj == null)
                return false;
            if (obj instanceof Message) {
                Message msg = (Message) obj;
                offerLocal(msg);
            } else {
                final Queue<Message> msgs = (Queue<Message>) obj;
                offerLocal(msgs);
            }
        }
        return true;
    }

    @Override
    public Message poll() {
        if (!hasWork())
            return null;
        Message msg = localNoResponsePendingQueue.poll();
        if (msg != null) {
            return msg;
        } else {
            return localResponsePendingQueue.poll();
        }
    }

    @Override
    public void requestBegin() {
        if (processingRequest)
            throw new IllegalStateException("already processing a request");
        processingRequest = true;
    }

    @Override
    public void requestEnd() {
        if (!processingRequest)
            throw new IllegalStateException("not processing a request");
        processingRequest = false;
    }
}
