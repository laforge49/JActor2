package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.impl.mtMessages.RequestMtImpl;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;

/**
 * A reactor bound to a thread.
 */
public class ThreadBoundReactorMtImpl extends ReactorMtImpl {

    private static final ThreadLocal<ThreadBoundReactorMtImpl> threadReactor = new ThreadLocal<ThreadBoundReactorMtImpl>();

    /**
     * Returns the ThreadBoundReactorMtImpl bound to the current thread.
     *
     * @return The ThreadBoundReactorMtImpl bound to the current thread, or null.
     */
    public static ThreadBoundReactorMtImpl threadReactor() {
        return threadReactor.get();
    }

    /**
     * Unbind the ThreadBoundReactorMtImpl from any thread.
     */
    public static void removeReactor() {
        threadReactor.remove();
    }

    /**
     * The boundProcessor.run method is called when there are messages to be processed.
     */
    private final Runnable boundProcessor;

    /**
     * Create a ThreadBoundReactorMtImpl.
     *
     * @param _parentReactor         The parent reactor.
     * @param _initialOutboxSize     The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize The initial local queue size.
     * @param _boundProcessor        The Runnable used when there are messages to be processed.
     */
    public ThreadBoundReactorMtImpl(final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize,
            final Runnable _boundProcessor) {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize);
        boundProcessor = _boundProcessor;
    }

    @Override
    public ThreadBoundReactor asReactor() {
        return (ThreadBoundReactor) getReactor();
    }

    @Override
    public void run() {
        threadReference.set(Thread.currentThread());
        threadReactor.set(this);
        super.run();
        threadReactor.remove();
        threadReference.set(null);
    }

    @Override
    protected void notBusy() throws Exception {
        flush();
    }

    /**
     * Returns true when there is code to be executed when the inbox is emptied.
     *
     * @return True when there is code to be executed when the inbox is emptied.
     */
    public boolean isIdler() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new CommonInbox(_initialLocalQueueSize);
    }

    @Override
    protected void afterAdd() {
        boundProcessor.run();
    }

    /**
     * The flush method disburses all buffered message to their target targetReactor for
     * processing.
     * <p>
     * The flush method is automatically called when there are
     * no more messages to be processed.
     * </p>
     *
     * @return True when one or more buffered messages were delivered.
     */
    public final boolean flush() throws Exception {
        boolean result = false;
        final Iterator<Map.Entry<ReactorMtImpl, ArrayDeque<RequestMtImpl<?>>>> iter = outbox
                .getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<ReactorMtImpl, ArrayDeque<RequestMtImpl<?>>> entry = iter
                        .next();
                final ReactorMtImpl target = entry.getKey();
                final ArrayDeque<RequestMtImpl<?>> messages = entry.getValue();
                iter.remove();
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }
}
