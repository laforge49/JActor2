package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.plant.PoolThread;
import org.agilewiki.jactor2.core.reactors.NonBlockingInbox;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadBoundReactorImpl extends ReactorImpl {

    /**
     * The boundProcessor.run method is called when there are messages to be processed.
     */
    private final Runnable boundProcessor;

    public ThreadBoundReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                  final int _initialOutboxSize, final int _initialLocalQueueSize,
                                  final Runnable _boundProcessor) throws Exception {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
        boundProcessor = _boundProcessor;
    }

    @Override
    public ThreadBoundReactor asReactor() {
        return (ThreadBoundReactor) getReactor();
    }

    @Override
    protected void notBusy() throws Exception {
        flush();
    }

    @Override
    public AtomicReference<PoolThread> getThreadReference() {
        throw new UnsupportedOperationException();
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
    protected void afterAdd() throws Exception {
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
