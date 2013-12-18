package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;

import javax.swing.*;

public class SwingBoundReactorImpl extends ThreadBoundReactorImpl {

    public SwingBoundReactorImpl(final Facility _facility,
                                  final int _initialOutboxSize, final int _initialLocalQueueSize) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, null);
    }

    @Override
    protected void afterAdd() {
        SwingUtilities.invokeLater(this);
    }
}
