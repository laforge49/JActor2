package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.impl.reactorsImpl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;

import javax.swing.*;

/**
 * A reactor bound to the Swing UI thread.
 */
public class SwingBoundReactorImpl extends ThreadBoundReactorImpl {

    /**
     * Returns the SwingBoundReactorImpl bound to the current thread.
     *
     * @return A SwingBoundReactorImpl, or null.
     */
    public static SwingBoundReactorImpl threadReactor() {
        return (SwingBoundReactorImpl) ThreadBoundReactorImpl.threadReactor();
    }

    /**
     * Create a SwingBoundReactorImpl.
     *
     * @param _parentReactorImpl     The parent reactor.
     * @param _initialOutboxSize     The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize The initial local queue size.
     */
    public SwingBoundReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                 final int _initialOutboxSize, final int _initialLocalQueueSize) {
        super(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize, null);
    }

    @Override
    public SwingBoundReactor asReactor() {
        return (SwingBoundReactor) getReactor();
    }

    @Override
    protected void afterAdd() {
        SwingUtilities.invokeLater(this);
    }
}
