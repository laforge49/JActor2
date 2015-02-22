package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;

/**
 * A swing-bound blade processes the requests and responses that it receives
 * on the swing UI thread.
 */
@GwtIncompatible
public class SwingBoundBladeBase extends BladeBase implements SwingBoundBlade {
    /**
     * Create a Swing bound blade and a Swing bound reactor whose parent is the internal reactor of Plant.
     */
    public SwingBoundBladeBase() throws Exception {
        _initialize(new SwingBoundReactor());
    }

    /**
     * Create a Swing bound blade.
     *
     * @param _reactor The blade's facility.
     */
    public SwingBoundBladeBase(final SwingBoundReactor _reactor) {
        _initialize(_reactor);
    }

    @Override
    public SwingBoundReactor getReactor() {
        return (SwingBoundReactor) super.getReactor();
    }
}
