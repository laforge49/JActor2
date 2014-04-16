package org.agilewiki.jactor2.core.impl.stReactors;

import org.agilewiki.jactor2.core.requests.RequestImpl;

/**
 * Provides at least one queue for a targetReactor's incoming messages, where the queue is a
 * local queues for messages that are passed using the targetReactor's own thread.
 *
 * @author monster
 */
public abstract class Inbox implements AutoCloseable {

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
     * Add a message to the appropriate doLocal queue.
     *
     * @param msg The message to be added.
     */
    protected abstract void offerLocal(final RequestImpl msg);

    /**
     * Retrieves and removes from the inbox the next message to be processed, or returns
     * null if there are no messages that can be processed.
     *
     * @return The next message to be processed, or null if there are no messages to be
     * processed.
     */
    abstract public RequestImpl poll();

    /**
     * Signals the start of a request.
     */
    public void requestBegin(final RequestImpl _requestImpl) {

    }

    /**
     * Signals that the result of a request has been assigned.
     */
    public void requestEnd(final RequestImpl _message) {

    }

    /**
     * Close all messages in the inbox.
     */
    @Override
    public void close() {
        while (true) {
            final RequestImpl message = poll();
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
