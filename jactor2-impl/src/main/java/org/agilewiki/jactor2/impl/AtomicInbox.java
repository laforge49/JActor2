package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Message;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AtomicInbox extends ConcurrentLinkedQueue<Object>
        implements Inbox {

    private boolean processingRequest;

    /**
     * Local response pending queue for same-thread exchanges.
     */
    private final ArrayDeque<Message> localResponsePendingQueue;

    /**
     * Local no response pending queue for same-thread exchanges.
     */
    private final ArrayDeque<Message> localNoResponsePendingQueue;

    /**
     * Creates a NonBlockingInbox, with the given local queue initial size.
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

    private void offerLocal(final Queue<Message> msgs) {
        while (!msgs.isEmpty()) {
            Message msg = msgs.poll();
            offerLocal(msg);
        }
    }

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
    public boolean isIdle() {
        return !processingRequest &&
                localResponsePendingQueue.isEmpty() &&
                localNoResponsePendingQueue.isEmpty() &&
                peek() == null;
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

    /**
     * Removes a message from the inbox, if the inbox is not empty.
     *
     * @return A message, or null.
     */
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
