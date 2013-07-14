package org.agilewiki.jactor2.impl;

import java.util.Queue;

/**
 * A message queue used in the Mailbox.
 *
 * @author monster
 */
public interface Inbox {
    /**
     * How big should the initial local queue size be?
     */
    int INITIAL_LOCAL_QUEUE_SIZE = 16;

    /**
     * How big should the initial (per target Mailbox) buffer size be?
     */
    int INITIAL_BUFFER_SIZE = 16;

    /**
     * Is the queue empty?
     */
    boolean isNonEmpty();

    /**
     * Inserts a new message in the queue.
     *
     * @param _local Should be true for same-mailbox exchanges
     * @param _msg   The new message
     */
    void offer(final boolean _local, final Message _msg);

    /**
     * Inserts a new message in the queue.
     * Multi-offer assumes the messages are not local.
     *
     * @param _msgs The new messages
     */
    void offer(final Queue<Message> _msgs);

    /**
     * Retrieves and removes the head of this queue, or returns null if this queue is empty.
     *
     * @return the head of this queue, or null if this queue is empty
     */
    Message poll();
}
