package org.agilewiki.pactor.impl;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

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

        /** Creates a new Message instance. */
        @Override
        public Message createMessage(final MessageSource source,
                final Message old, final Request<?> _request,
                final ExceptionHandler handler, final ResponseProcessor<?> rp) {
            return new Message(source, old, _request, handler, rp);
        }

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
