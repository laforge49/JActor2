package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Message;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AtomicInbox extends ConcurrentLinkedQueue<Object>
        implements Inbox {

    /**
     * Local queue for same-thread exchanges.
     */
    private final ArrayDeque<Object> localQueue;

    /**
     * Creates a NonBlockingInbox, with the given local queue initial size.
     */
    public AtomicInbox(final int initialLocalQueueSize) {
        if (initialLocalQueueSize > INITIAL_LOCAL_QUEUE_SIZE)
            localQueue = new ArrayDeque<Object>(initialLocalQueueSize);
        else
            localQueue = new ArrayDeque<Object>(INITIAL_LOCAL_QUEUE_SIZE);
    }

    @Override
    public boolean hasWork() {
        //ConcurrentLinkedQueue.isEmpty() is not accurate enough
        return !localQueue.isEmpty() || peek() != null;
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
            localQueue.offer(msg);
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

    /**
     * Removes a message from the inbox, if the inbox is not empty.
     *
     * @return A message, or null.
     */
    @Override
    public Message poll() {
        Object obj = localQueue.peek();
        if (obj == null) {
            obj = super.poll();
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
                        // msgs is not empty so save it in localQueue
                        localQueue.offer(msgs);
                    }
                    return result;
                }
            }
        } else {
            if (obj instanceof Message) {
                return (Message) localQueue.poll();
            } else {
                @SuppressWarnings("unchecked")
                final Queue<Message> msgs = (Queue<Message>) obj;
                final Message result = msgs.poll();
                if (msgs.isEmpty()) {
                    // msgs is empty, so remove msgs from localQueue
                    localQueue.poll();
                }
                return result;
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
