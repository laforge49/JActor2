package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Message;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AtomicInbox extends ConcurrentLinkedQueue<Object>
        implements Inbox {

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
    public boolean hasWork() {
        //ConcurrentLinkedQueue.isEmpty() is not accurate enough
        return !localResponsePendingQueue.isEmpty() || !localNoResponsePendingQueue.isEmpty() || peek() != null;
    }

    @Override
    public boolean isIdle() {
        return !hasWork();
    }

    /**
     * Adds a new message to the inbox.
     *
     * @param local Should be true for same-thread exchanges
     * @param msg   The new message
     */
    @Override
    public void offer(final boolean local, final Message msg) {
        if (local) {
            if (msg.isResponsePending())
                localResponsePendingQueue.offer(msg);
            else
                localNoResponsePendingQueue.offer(msg);
        } else {
            super.offer(msg);
        }
    }

    /**
     * Adds a new message to the inbox.
     *
     * @param msgs The new messages
     */
    @Override
    public void offer(final Queue<Message> msgs) {
        if (!msgs.isEmpty()) {
            super.add(msgs);
        }
    }

    private void offerLocal(final Queue<Message> msgs) {
        while (!msgs.isEmpty()) {
            Message msg = msgs.poll();
            if (msg.isResponsePending())
                localResponsePendingQueue.offer(msg);
            else
                localNoResponsePendingQueue.offer(msg);
        }
    }

    /**
     * Removes a message from the inbox, if the inbox is not empty.
     *
     * @return A message, or null.
     */
    @Override
    public Message poll() {
        Message msg = localNoResponsePendingQueue.peek();
        if (msg != null) {
            return (Message) localNoResponsePendingQueue.poll();
        } else {
            msg = localResponsePendingQueue.peek();
            if (msg != null) {
                return (Message) localResponsePendingQueue.poll();
            } else {
                Object obj = super.poll();
                if (obj == null) {
                    return null;
                } else {
                    if (obj instanceof Message) {
                        return (Message) obj;
                    } else {
                        @SuppressWarnings("unchecked")
                        final Queue<Message> msgs = (Queue<Message>) obj;
                        final Message result = msgs.poll();
                        if (!msgs.isEmpty()) {
                            offerLocal(msgs);
                        }
                        return result;
                    }
                }
            }
        }
    }

    @Override
    public void requestBegin() {
    }

    @Override
    public void requestEnd() {
    }
}
