package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.reactors.IsolationReactor;

/**
 * <p>
 *     An isolation blade does not process a request until the response for the previous
 *     request is returned.
 * </p>
 */
public interface IsolationBlade extends Blade {
    /**
     * Returns the isolation reactor used by this blade.
     *
     * @return The IsolationReactor.
     */
    @Override
    IsolationReactor getReactor();
}
