package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.messaging.Message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Provides at least two queues for a message processor's incoming messages, where the first queue is a
 * concurrent linked queue for messages passed from other message processors and the other(s) are
 * local queues for messages that are passed using the message processor's own thread.
 *
 * @author monster
 */
public abstract class Inbox implements AutoCloseable {
    /**
     * Default initial local queue size.
     */
    public static int DEFAULT_INITIAL_LOCAL_QUEUE_SIZE = 16;

    /**
     * Concurrent queue for cross-thread exchanges.
     */
    protected ConcurrentLinkedQueue<Object> concurrentQueue;

    /**
     * Returns true when a message has been passed from another thread.
     * @return True when a message has been passed from another thread.
     */
    public boolean hasConcurrent() {
        return concurrentQueue.peek() != null;
    }

    /**
     * Returns true when there is a message in the inbox that can be processed.
     * (This method is not thread safe and must be called on the message processor's thread.)
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
     * in isolation.
     *
     * @return True when there is no work pending.
     */
    abstract public boolean isIdle();

    /**
     * Inserts a new message in the queue.
     *
     * @param _local True when the message is being inserted using the message processor's own thread.
     * @param _msg   The new message.
     */
    public void offer(final boolean _local, final Message _msg) {
        if (_local) {
            offerLocal(_msg);
        } else {
            concurrentQueue.offer(_msg);
        }
    }

    /**
     * Thread-safe message insertion.
     *
     * @param _msgs The new messages.
     */
    public void offer(final Queue<Message> _msgs) {
        if (!_msgs.isEmpty()) {
            concurrentQueue.add(_msgs);
        }
    }

    /**
     * Add a message to the appropriate local queue.
     *
     * @param msg The message to be added.
     */
    protected abstract void offerLocal(final Message msg);

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
    public void requestBegin() {

    }

    /**
     * Signals that the result of a request has been assigned.
     */
    public void requestEnd() {

    }

    @Override
    public void close() {
        while (true) {
            final Message message = poll();
            if (message == null)
                return;
            if (message.isForeign() && message.isResponsePending())
                try {
                    message.close();
                } catch (final Throwable t) {
                }
        }
    }
}
