package org.agilewiki.pactor.impl;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Creates default MessageQueues instances.
 *
 * @author monster
 */
public class MessageQueueFactoryImpl implements MessageQueueFactory {
    private static final class DefaultMessageQueue extends
            ConcurrentLinkedQueue<Message> implements MessageQueue {
        /**  */
        private static final long serialVersionUID = 1L;

        /** Is the queue empty? */
        @Override
        public boolean isNonEmpty() {
            return !isEmpty();
        }
    }

    @Override
    public MessageQueue createMessageQueue() {
        return new DefaultMessageQueue();
    }

    /** Singleton instance. */
    public static final MessageQueueFactory INTANCE = new MessageQueueFactoryImpl();
}
