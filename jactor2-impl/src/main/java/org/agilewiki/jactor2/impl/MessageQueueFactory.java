package org.agilewiki.jactor2.impl;

/**
 * Creates MessageQueues.
 *
 * @author monster
 */
public interface MessageQueueFactory {
    /**
     * Creates a new Inbox instance.
     *
     * @param initialLocalQueueSize The initial number of slots in the local queue.
     */
    Inbox createMessageQueue(final int initialLocalQueueSize);
}
