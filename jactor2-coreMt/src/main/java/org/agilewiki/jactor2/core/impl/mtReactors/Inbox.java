package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.impl.mtMessages.RequestMtImpl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Provides at least two queues for a targetReactor's incoming messages, where the first queue is a
 * concurrent linked queue for messages passed from other reactors and the other(s) are
 * local queues for messages that are passed using the targetReactor's own thread.
 *
 * @author monster
 */
public abstract class Inbox implements AutoCloseable {

    /**
     * Concurrent queue for cross-thread exchanges.
     */
    protected ConcurrentLinkedQueue<Object> concurrentQueue;

    /**
     * Returns true when a message has been passed from another thread.
     *
     * @return True when a message has been passed from another thread.
     */
    public boolean hasConcurrent() {
        return concurrentQueue.peek() != null;
    }

    /**
     * Returns true when there is a message in the inbox that can be processed.
     * (This method is not thread safe and must be called on the targetReactor's thread.)
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
     * @param _local True when the message is being inserted using the targetReactor's own thread.
     * @param _msg   The new message.
     */
    public void offer(final boolean _local, final RequestMtImpl<?> _msg) {
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
    public void offer(final Queue<RequestMtImpl<?>> _msgs) {
        if (!_msgs.isEmpty()) {
            concurrentQueue.add(_msgs);
        }
    }

    /**
     * Add a message to the appropriate doLocal queue.
     *
     * @param msg The message to be added.
     */
    protected abstract void offerLocal(final RequestMtImpl<?> msg);

    /**
     * Retrieves and removes from the inbox the next message to be processed, or returns
     * null if there are no messages that can be processed.
     *
     * @return The next message to be processed, or null if there are no messages to be
     * processed.
     */
    abstract public RequestMtImpl<?> poll();

    /**
     * Signals the start of a request.
     */
    public void requestBegin(final RequestMtImpl<?> _requestImpl) {

    }

    /**
     * Signals that the result of a request has been assigned.
     */
    public void requestEnd(final RequestMtImpl<?> _message) {

    }

    /**
     * Close all messages in the inbox.
     */
    @Override
    public void close() {
        while (true) {
            final RequestMtImpl<?> message = poll();
            if (message == null) {
                return;
            }
            if (!message.isComplete()) {
                try {
                    message.close();
                } catch (final Throwable t) {
                }
            }
        }
    }
}
