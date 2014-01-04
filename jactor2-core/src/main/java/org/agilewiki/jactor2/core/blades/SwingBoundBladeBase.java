package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;

public class SwingBoundBladeBase extends BladeBase implements SwingBoundBlade {
    public SwingBoundBladeBase() throws Exception {
        _initialize(new SwingBoundReactor());
    }

    public SwingBoundBladeBase(final SwingBoundReactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    @Override
    public SwingBoundReactor getReactor() {
        return (SwingBoundReactor) super.getReactor();
    }
}
