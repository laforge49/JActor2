package org.agilewiki.pamailbox;

/**
 * Creates ArrayDeque MessageQueues instances.
 *
 * @author monster
 */
public class DefaultMessageQueueFactoryImpl implements MessageQueueFactory {
    /**
     * Creates a new MessageQueue instance.
     *
     * @param initialLocalQueueSize The initial number of slots in the local queue.
     */
    @Override
    public MessageQueue createMessageQueue(final int initialLocalQueueSize) {
        return new DefaultMessageQueue(initialLocalQueueSize);
    }
}
