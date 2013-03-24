package org.agilewiki.pactor.impl;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.agilewiki.pactor.Message;
import org.agilewiki.pactor.MessageSource;
import org.agilewiki.pactor.MessageQueue;
import org.agilewiki.pactor.MessageQueueFactory;
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

        private static final Message[] EMPTY = new Message[0];

        private Message[] localQueue = EMPTY;

        private int head;
        private int tail;
        private int count;

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
            return (count > 0) || !isEmpty();
        }

        /**
         *
         */
        @Override
        public void offer(final Message e, final boolean local) {
            if (local) {
                final int length = localQueue.length;
                if (count == length) {
                    final Message[] newLocalQueue = new Message[1 + length * 2];
                    if (head <= tail) {
                        System.arraycopy(localQueue, head, newLocalQueue, 0,
                                count);
                        head = 0;
                        tail = count;
                    } else {
                        System.arraycopy(localQueue, head, newLocalQueue, 0,
                                length - head);
                        System.arraycopy(localQueue, 0, newLocalQueue,
                                tail - 1, tail);
                    }
                    localQueue = newLocalQueue;
                }
                localQueue[tail] = e;
                tail = (tail + 1) % localQueue.length;
                count++;
            } else {
                super.offer(e);
            }
        }

        @Override
        public Message poll() {
            if (count > 0) {
                final Message result = localQueue[head];
                head = (head + 1) % localQueue.length;
                count--;
                return result;
            }
            return super.poll();
        }
    }

    @Override
    public MessageQueue createMessageQueue() {
        return new DefaultMessageQueue();
    }

    /** Singleton instance. */
    public static final MessageQueueFactory INTANCE = new MessageQueueFactoryImpl();
}
