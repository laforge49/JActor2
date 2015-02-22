package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;

/**
 * A swing-bound blade processes the requests and responses that it receives
 * on the swing UI thread.
 */
@GwtIncompatible
public interface SwingBoundBlade extends Blade {
    /**
     * Returns the swing-bound reactor used by this blade.
     *
     * @return The SwingBoundReactor.
     */
    @Override
    SwingBoundReactor getReactor();
}
