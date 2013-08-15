package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.messaging.Message;

import java.util.Queue;

/**
 * Provides at least two queues for a processing's incoming messages, where the first queue is a
 * concurrent linked queue for messages passed from other mailboxes and the other(s) are
 * local queues for messages that are passed using the processing's own thread.
 *
 * @author monster
 */
public abstract class Inbox {
    /**
     * Default initial local queue size.
     */
    public static int INITIAL_LOCAL_QUEUE_SIZE = 16;

    /**
     * Returns true when there is a message in the inbox that can be processed.
     * (This method is not thread safe and must be called on the processing's thread.)
     *
     * @return True if there is a message in the inbox that can be processed.
     */
    abstract public boolean hasWork();

    /**
     * Returns true when all the queues are empty.
     *
     * @return True when all the queues are empty.
     */
    abstract public boolean isEmpty();

    /**
     * Returns true when the inbox is empty and no request messages are being processed
     * atomically.
     *
     * @return True when there is no work pending.
     */
    abstract public boolean isIdle();

    /**
     * Inserts a new message in the queue.
     *
     * @param _local True when the message is being inserted using the processing's own thread.
     * @param _msg   The new message.
     */
    abstract public void offer(final boolean _local, final Message _msg);

    /**
     * Thread-safe message insertion.
     *
     * @param _msgs The new messages.
     */
    abstract public void offer(final Queue<Message> _msgs);

    /**
     * Retrieves and removes from the inbox the next message to be processed, or returns
     * null if there are no messages that can be processed.
     *
     * @return The next message to be processed, or null if there are no messages to be
     *         processed.
     */
    abstract public Message poll();

    /**
     * Signals the start of a request.
     */
    abstract public void requestBegin();

    /**
     * Signals that the result of a request has been assigned.
     */
    abstract public void requestEnd();
}
