package org.agilewiki.pactor.impl;

/**
 * A concurrent message queue, used in the Mailbox.
 *
 * @author monster
 */
public interface MessageQueue {
    /** Is the queue empty? */
    boolean isNonEmpty();

    /**
     * Inserts the specified message into this queue.
     * Always returns true.
     */
    boolean add(final Message e);

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     * @return the head of this queue, or null if this queue is empty
     */
    Message poll();
}
