package org.agilewiki.jactor2.core.impl;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingInbox;

public class NonBlockingReactorImpl extends UnboundReactorImpl {

    private final boolean internalReactor;

    public NonBlockingReactorImpl(final Facility _facility,
                                  final int _initialOutboxSize, final int _initialLocalQueueSize,
                                  final Runnable _onIdle) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
        internalReactor = false;
    }

    public NonBlockingReactorImpl(final Facility _facility,
                                  final int _initialOutboxSize, final int _initialLocalQueueSize,
                                  final Runnable _onIdle, final boolean _internalReactor) throws Exception {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, _onIdle);
        internalReactor = true;
        nonfunctional = true;
    }

    @Override
    protected Inbox createInbox(int _initialLocalQueueSize) {
        return new NonBlockingInbox(_initialLocalQueueSize);
    }

    /**
     * No autoclose.
     */
    @Override
    protected void addClose() throws Exception {
        if (!internalReactor)
            super.addClose();
    }
}
