package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.messaging.Message;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The inbox used by AtomicMailbox, two ArrayDeques are used as the local queues, one for
 * requests and the other for events and responses.
 */
public class AtomicInbox extends ConcurrentLinkedQueue<Object>
        implements Inbox {

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
        if (initialLocalQueueSize > INITIAL_LOCAL_QUEUE_SIZE) {
            localResponsePendingQueue = new ArrayDeque<Message>(initialLocalQueueSize);
            localNoResponsePendingQueue = new ArrayDeque<Message>(initialLocalQueueSize);
        } else {
            localResponsePendingQueue = new ArrayDeque<Message>(INITIAL_LOCAL_QUEUE_SIZE);
            localNoResponsePendingQueue = new ArrayDeque<Message>(INITIAL_LOCAL_QUEUE_SIZE);
        }
    }

    @Override
    public void offer(final boolean local, final Message msg) {
        if (local) {
            offerLocal(msg);
        } else {
            super.offer(msg);
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

    /**
     * Add a message to the appropriate local queue.
     *
     * @param msg The message to be added.
     */
    private void offerLocal(final Message msg) {
        if (msg.isResponsePending())
            localResponsePendingQueue.offer(msg);
        else
            localNoResponsePendingQueue.offer(msg);
    }

    @Override
    public void offer(final Queue<Message> msgs) {
        if (!msgs.isEmpty()) {
            super.add(msgs);
        }
    }

    @Override
    public boolean isEmpty() {
        return localResponsePendingQueue.isEmpty() &&
                localNoResponsePendingQueue.isEmpty() &&
                peek() == null;
    }

    @Override
    public boolean isIdle() {
        return !processingRequest && isEmpty();
    }

    @Override
    public boolean hasWork() {
        while (localNoResponsePendingQueue.isEmpty() &&
                (processingRequest || localResponsePendingQueue.isEmpty())) {
            Object obj = super.poll();
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
            throw new IllegalStateException("already processing request");
        processingRequest = true;
    }

    @Override
    public void requestEnd() {
        if (!processingRequest)
            throw new IllegalStateException("not processing request");
        processingRequest = false;
    }
}
