package org.agilewiki.jactor2.core.reactors.impl;

import org.agilewiki.jactor2.core.requests.impl.RequestImpl;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;

public class ThreadBoundReactorImpl extends ReactorImpl {

    private static final ThreadLocal<ThreadBoundReactorImpl> threadReactor =
            new ThreadLocal<ThreadBoundReactorImpl>();

    public static ThreadBoundReactorImpl threadReactor() {
        return threadReactor.get();
    }

    public static void removeReactor() {
        threadReactor.remove();
    }

    /**
     * The boundProcessor.run method is called when there are messages to be processed.
     */
    private final Runnable boundProcessor;

    public ThreadBoundReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                  final int _initialOutboxSize, final int _initialLocalQueueSize,
                                  final Runnable _boundProcessor) {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
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

    @Override
    public boolean isIdler() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Inbox createInbox(final int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
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
        final Iterator<Map.Entry<ReactorImpl, ArrayDeque<RequestImpl>>> iter = outbox
                .getIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                result = true;
                final Map.Entry<ReactorImpl, ArrayDeque<RequestImpl>> entry = iter
                        .next();
                final ReactorImpl target = entry.getKey();
                final ArrayDeque<RequestImpl> messages = entry.getValue();
                iter.remove();
                target.unbufferedAddMessages(messages);
            }
        }
        return result;
    }
}