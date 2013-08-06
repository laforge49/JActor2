package org.agilewiki.jactor2.core.mailbox;

import org.agilewiki.jactor2.core.messaging.Message;

import java.util.Queue;

/**
 * Provides at least two queues for a mailbox's incoming messages, where the first queue is a
 * concurrent linked queue for messages passed from other mailboxes and the other(s) are
 * local queues for messages that are passed using the mailbox's own thread.
 *
 * @author monster
 */
public interface Inbox {
    /**
     * Default initial local queue size.
     */
    int INITIAL_LOCAL_QUEUE_SIZE = 16;

    /**
     * Default initial (per target Mailbox) buffer.
     */
    int INITIAL_BUFFER_SIZE = 16;

    /**
     * Returns true when there is a message in the inbox that can be processed.
     * (This method is not thread safe and must be called on the mailbox's thread.)
     *
     * @return True if there is a message in the inbox that can be processed.
     */
    boolean hasWork();

    /**
     * Returns true when all the queues are empty.
     *
     * @return True when all the queues are empty.
     */
    boolean isEmpty();

    /**
     * Returns true when the inbox is empty and no request messages are being processed
     * atomically.
     *
     * @return True when there is no work pending.
     */
    boolean isIdle();

    /**
     * Inserts a new message in the queue.
     *
     * @param _local True when the message is being inserted using the mailbox's own thread.
     * @param _msg   The new message.
     */
    void offer(final boolean _local, final Message _msg);

    /**
     * Inserts new messages in the concurrent linked queue.
     *
     * @param _msgs The new messages.
     */
    void offer(final Queue<Message> _msgs);

    /**
     * Retrieves and removes from the inbox the next message to be processed, or returns
     * null if there are no messages that can be processed.
     *
     * @return The next message to be processed, or null if there are no messages to be
     *         processed.
     */
    Message poll();

    /**
     * Signals the start of a request.
     */
    void requestBegin();

    /**
     * Signals that the result of a request has been assigned.
     */
    void requestEnd();
}
