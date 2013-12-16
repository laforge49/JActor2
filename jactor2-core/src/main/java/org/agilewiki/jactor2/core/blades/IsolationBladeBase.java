package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class IsolationBladeBase extends BladeBase implements IsolationBlade {
    public void initialize(final IsolationReactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    @Override
    public IsolationReactor getReactor() {
        return (IsolationReactor) super.getReactor();
    }
}
