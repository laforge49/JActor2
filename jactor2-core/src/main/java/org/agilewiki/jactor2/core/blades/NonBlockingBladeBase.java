package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * <p>
 * A blades which does not perform long computations nor otherwise block the thread.
 * </p>
 */
public class NonBlockingBladeBase extends BladeBase implements NonBlockingBlade {
    public NonBlockingBladeBase() throws Exception {
        _initialize(new NonBlockingReactor());
    }

    public NonBlockingBladeBase(final NonBlockingReactor _nonBlockingReactor) throws Exception {
        _initialize(_nonBlockingReactor);
    }

    @Override
    public NonBlockingReactor getReactor() {
        return (NonBlockingReactor) super.getReactor();
    }
}
