package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * <p>
 * A blades which does not perform long computations nor otherwise block the thread.
 * </p>
 */
public class NonBlockingBladeBase extends BladeBase implements NonBlockingBlade {
    /**
     * Create a Blocking blade and a Blocking reactor whose parent is the internal reactor of Plant.
     */
    public NonBlockingBladeBase() throws Exception {
        _initialize(new NonBlockingReactor());
    }

    /**
     * Create a Blocking blade.
     *
     * @param _reactor The blade's facility.
     */
    public NonBlockingBladeBase(final NonBlockingReactor _reactor) throws Exception {
        _initialize(_reactor);
    }

    @Override
    public NonBlockingReactor getReactor() {
        return (NonBlockingReactor) super.getReactor();
    }
}
