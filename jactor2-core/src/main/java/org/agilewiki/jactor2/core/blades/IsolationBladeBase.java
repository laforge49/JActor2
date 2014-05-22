package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * <p>
 *     An isolation blade does not process a request until the response for the previous
 *     request is returned.
 * </p>
 */
public class IsolationBladeBase extends BladeBase implements IsolationBlade {
    /**
     * Create an Isolation blade and an Isolation reactor whose parent is the internal reactor of Plant.
     */
    public IsolationBladeBase() throws Exception {
        _initialize(new IsolationReactor());
    }

    /**
     * Create an Isolation blade.
     *
     * @param _reactor The blade's facility.
     */
    public IsolationBladeBase(final IsolationReactor _reactor) {
        _initialize(_reactor);
    }

    @Override
    public IsolationReactor getReactor() {
        return (IsolationReactor) super.getReactor();
    }
}
