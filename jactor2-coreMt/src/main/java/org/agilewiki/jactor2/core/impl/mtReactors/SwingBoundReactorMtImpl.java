package org.agilewiki.jactor2.core.impl.mtReactors;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;

import javax.swing.*;

/**
 * A reactor bound to the Swing UI thread.
 */
public class SwingBoundReactorMtImpl extends ThreadBoundReactorMtImpl {

    /**
     * Returns the SwingBoundReactorMtImpl bound to the current thread.
     *
     * @return A SwingBoundReactorMtImpl, or null.
     */
    public static SwingBoundReactorMtImpl threadReactor() {
        return (SwingBoundReactorMtImpl) ThreadBoundReactorMtImpl
                .threadReactor();
    }

    /**
     * Create a SwingBoundReactorMtImpl.
     *
     * @param _parentReactor         The parent reactor.
     * @param _initialOutboxSize     The initial buffer size for outgoing messages.
     * @param _initialLocalQueueSize The initial local queue size.
     */
    public SwingBoundReactorMtImpl(final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize) {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize, null);
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
