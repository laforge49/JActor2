package org.agilewiki.jactor2.impl;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A default <code>Inbox</code> implementation, using a
 * ConcurrentLinkedQueue for cross-mailbox message exchanges, and a ArrayDeque
 * for same-mailbox message exchanges.
 *
 * @author monster
 */
public class DefaultMessageQueue extends ConcurrentLinkedQueue<Object>
        implements Inbox {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Local queue for same-mailbox exchanges.
     */
    private final ArrayDeque<Object> localQueue;

    /**
     * Creates a DefaultMessageQueue, with the given local queue initial size.
     */
    public DefaultMessageQueue(final int initialLocalQueueSize) {
        if (initialLocalQueueSize > INITIAL_LOCAL_QUEUE_SIZE)
            localQueue = new ArrayDeque<Object>(initialLocalQueueSize);
        else
            localQueue = new ArrayDeque<Object>(INITIAL_LOCAL_QUEUE_SIZE);
    }

    /**
     * Is the queue empty?
     */
    @Override
    public boolean isNonEmpty() {
        //ConcurrentLinkedQueue.isEmpty() is not accurate enough
        return !localQueue.isEmpty() || peek() != null;
    }

    /**
     * Inserts a new message in the queue.
     *
     * @param local Should be true for same-mailbox exchanges
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
     * Inserts a new message in the queue.
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
     * Returns one message, if any is available.
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
}
