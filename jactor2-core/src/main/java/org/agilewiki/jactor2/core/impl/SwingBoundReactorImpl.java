package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

import javax.swing.*;

public class SwingBoundReactorImpl extends ThreadBoundReactorImpl {

    public static SwingBoundReactorImpl threadReactor() {
        return (SwingBoundReactorImpl) ThreadBoundReactorImpl.threadReactor();
    }

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
