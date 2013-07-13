package org.agilewiki.jactor2.impl;

/**
 * Creates ArrayDeque MessageQueues instances.
 *
 * @author monster
 */
public class DefaultMessageQueueFactoryImpl implements MessageQueueFactory {
    /**
     * Creates a new Inbox instance.
     *
     * @param initialLocalQueueSize The initial number of slots in the local queue.
     */
    @Override
    public Inbox createMessageQueue(final int initialLocalQueueSize) {
        return new DefaultMessageQueue(initialLocalQueueSize);
    }
}
