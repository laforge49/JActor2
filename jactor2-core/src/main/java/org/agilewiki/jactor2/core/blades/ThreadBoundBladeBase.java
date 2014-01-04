package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;

public class ThreadBoundBladeBase extends BladeBase implements ThreadBoundBlade {
    public ThreadBoundBladeBase() throws Exception {
        _initialize(new ThreadBoundReactor());
    }

    public ThreadBoundBladeBase(final ThreadBoundReactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    public void initialize(final ThreadBoundReactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    @Override
    public ThreadBoundReactor getReactor() {
        return (ThreadBoundReactor) super.getReactor();
    }
}
