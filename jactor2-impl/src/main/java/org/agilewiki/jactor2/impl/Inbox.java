package org.agilewiki.jactor2.impl;

import org.agilewiki.jactor2.api.Message;

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
     * Is there work that can be done?
     *
     * @return True when there is work ready to be done.
     */
    boolean hasWork();

    /**
     * Is nothing pending?
     *
     * @return True when there is no work pending.
     */
    boolean isIdle();

    /**
     * Inserts a new message in the queue.
     *
     * @param _local Should be true for same-mailbox exchanges
     * @param _msg   The new message
     */
    void offer(final boolean _local, final Message _msg);

    /**
     * Inserts new messages in the queue.
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

    /**
     * Signals the start of a request.
     */
    void requestBegin();

    /**
     * Signals that a request has completed.
     */
    void requestEnd();
}
