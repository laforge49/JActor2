package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.messages.Message;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The inbox used by NonBlockingReactor, ThreadBoundReactor
 * and SwingBoundReactor, NonBlockingInbox uses a single ArrayDeque for the doLocal queue.
 *
 * @author monster
 */
public class NonBlockingInbox extends Inbox {

    /**
     * Local queue for same-thread exchanges.
     */
    private final ArrayDeque<Object> localQueue;

    /**
     * Creates a NonBlockingInbox.
     *
     * @param initialLocalQueueSize The initial doLocal queue size.
     */
    public NonBlockingInbox(final int initialLocalQueueSize) {
        concurrentQueue = new ConcurrentLinkedQueue<Object>();
        localQueue = new ArrayDeque<Object>(initialLocalQueueSize);
    }

    @Override
    public boolean hasWork() {
        //ConcurrentLinkedQueue.isEmpty() is not accurate enough
        final boolean rv = !localQueue.isEmpty()
                || (concurrentQueue.peek() != null);
        return rv;
    }

    @Override
    public boolean isEmpty() {
        return hasWork();
    }

    @Override
    public boolean isIdle() {
        return !hasWork();
    }

    @Override
    protected void offerLocal(final Message msg) {
        localQueue.offer(msg);
    }

    @Override
    public Message poll() {
        Object obj = localQueue.peek();
        if (obj == null) {
            obj = concurrentQueue.poll();
            if (obj == null) {
                return null;
            } else {
                if (obj instanceof Message) {
                    return (Message) obj;
                } else {
                    @SuppressWarnings("unchecked")
                    final Queue<Message> msgs = (Queue<Message>) obj;
                    final Message result = msgs.poll();
                    if (!msgs.isEmpty()) {
                        // msgs is not empty so save it in localQueue
                        localQueue.offer(msgs);
                    }
                    return result;
                }
            }
        } else {
            if (obj instanceof Message) {
                return (Message) localQueue.poll();
            } else {
                @SuppressWarnings("unchecked")
                final Queue<Message> msgs = (Queue<Message>) obj;
                final Message result = msgs.poll();
                if (msgs.isEmpty()) {
                    // msgs is empty, so remove msgs from localQueue
                    localQueue.poll();
                }
                return result;
            }
        }
    }
}
