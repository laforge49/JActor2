package org.agilewiki.pamailbox;

/**
 * Creates MessageQueues.
 *
 * @author monster
 */
public interface MessageQueueFactory {
    /**
     * Creates a new MessageQueue instance.
     *
     * @param initialLocalQueueSize The initial number of slots in the local queue.
     */
    MessageQueue createMessageQueue(final int initialLocalQueueSize);
}
